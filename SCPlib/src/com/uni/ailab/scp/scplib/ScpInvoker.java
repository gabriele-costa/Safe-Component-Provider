package com.uni.ailab.scp.scplib;

import android.R;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Xml;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ServiceInfo;

import com.uni.ailab.scp.scplib.ScpComponent;
import com.uni.ailab.scp.scplib.ScpContext;
import com.uni.ailab.scp.scplib.ScpIntent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class ScpInvoker extends Activity{

	private ArrayList featuredComponents = new ArrayList<ScpComponent>();
    private String packageName;
    private String applicantName;
    private String applicantType = "Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName=getApplicationContext().getPackageName();
        processManifest(featuredComponents);

        ScpIntent i = createRegistrationIntent();
        new ScpContext(this).sendRegistrationRequest(i);
    }

    protected ScpIntent createRegistrationIntent () {
        ScpIntent i = new ScpIntent();
        i.setAction(ScpIntent.ACTION_SCP);
        i.putExtra("scp.type", applicantType);
        i.putExtra("scp.package", packageName);
        i.putExtra("scp.applicant", applicantName);
        i.putParcelableArrayListExtra("scp.featuredActivities", featuredComponents);
        return i;
    }

    public void addFeatComponent (String name, String type, String[] policies, String[] permissions, String[] actions) {
        Iterator<ScpComponent> itFC = featuredComponents.iterator();
        while (itFC.hasNext()) {
            if (itFC.next().getName().contains(name)) {
                return;
            }
        }
        this.featuredComponents.add(new ScpComponent(name, type, policies, permissions, actions));
        return;
    }

    public void setFeaturedComponents(ArrayList<ScpComponent> list){
        this.featuredComponents = list;
    }

    public void setApplicantData(Class c){
        setApplicantName(c.getName());
        if ((c.isAssignableFrom(Activity.class))||(c.getSuperclass().toString().contains("Activity"))){

            applicantType = "Activity";
        }
        else if ((c.isAssignableFrom(Service.class))||(c.getSuperclass().toString().contains("Service"))){

            applicantType = "Service";
        }
    }

    public void setApplicantName (String name){
        this.applicantName = name;
        return;
    }

    public void setPackageName (String name){
        this.packageName = name;
        return;
    }

    public void processManifest (ArrayList<ScpComponent> list) {
        if (list == null){
            list = this.featuredComponents;
        }
        PackageManager pm = this.getPackageManager();
        ActivityInfo[] appActs = new ActivityInfo[0];
        String[] appPerms = new String[0];
        try {
            PackageInfo i = pm.getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS);
            if (i.requestedPermissions != null){
                String[] perms = new String[i.requestedPermissions.length];
                int counter = 0;
                for(String pi : i.requestedPermissions){
                    perms[counter] = pi;
                    counter++;
                }
                appPerms = perms;
            }
            appActs = pm.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES|PackageManager.GET_META_DATA).activities;
            ServiceInfo[] appServs = pm.getPackageInfo(this.packageName, PackageManager.GET_SERVICES|PackageManager.GET_META_DATA).services;
            for (ActivityInfo ai : appActs) {
                String name = ai.name;
                String[] perms = appPerms;
                String[] actions = new String[0];
                String[] pols = new String[0];
                String actDeclPerm = ai.permission;
                if (actDeclPerm != null){
                    if (!actDeclPerm.contains("scp")) {
                        String policy = "D{[" + actDeclPerm + "]}";
                        String[] temp = new String[pols.length + 1];

                        for (int count = 0; count < pols.length; count++) {
                            temp[count] = pols[count];
                        }
                        temp[pols.length] = policy;
                        pols = temp;
                    }
                }
                    if (ai.metaData != null) {
                        if ((Object) ai.metaData.get("SCP-START") != null) {
                            boolean startString = (boolean) ai.metaData.get("SCP-START");
                            if (startString){
                                setApplicantName(ai.name);
                            }
                        }
                        if ((String) ai.metaData.get("SCP-POLS") != null) {
                            String polsString = (String) ai.metaData.get("SCP-POLS");
                            if (polsString.contains(";")) {
                                String[] separated = polsString.split(";");
                                String[] newPols = new String[pols.length+separated.length];
                                int pCount = 0;
                                for(String p : perms){
                                    newPols[pCount] = p;
                                    pCount++;
                                }
                                for (String p : separated){
                                    p = p.replace(" ","");
                                    newPols[pCount] = p;
                                    pCount++;
                                }
                                perms = newPols;
                            }
                            else{
                                String[] newPols = new String[perms.length+1];
                                int pCount = 0;
                                for(String p : perms){
                                    newPols[pCount] = p;
                                    pCount++;
                                }
                                newPols[pCount] = polsString;
                                perms = newPols;
                            }
                        }
                        if ((String) ai.metaData.get("SCP-PERMS") != null) {
                            String permString = (String) ai.metaData.get("SCP-PERMS");
                            if (permString.contains(";")) {
                                String[] separated = permString.split(";");
                                String[] newPerms = new String[perms.length+separated.length];
                                int pCount = 0;
                                for(String p : perms){
                                    newPerms[pCount] = p;
                                    pCount++;
                                }
                                for (String p : separated){
                                    p = p.replace(" ","");
                                    newPerms[pCount] = p;
                                    pCount++;
                                }
                                perms = newPerms;
                            }
                            else{
                                String[] newPerms = new String[perms.length+1];
                                int pCount = 0;
                                for(String p : perms){
                                    newPerms[pCount] = p;
                                    pCount++;
                                }
                                newPerms[pCount] = permString;
                                perms = newPerms;
                            }
                        }
                        if ((String) ai.metaData.get("SCP-ACTIONS") != null) {
                            String acString = (String) ai.metaData.get("SCP-ACTIONS");
                            if (acString.contains(";")) {
                                String[] separated = acString.split(";");
                                String[] newAcs = new String[actions.length+separated.length];
                                int aCount = 0;
                                for(String p : actions){
                                    newAcs[aCount] = p;
                                    aCount++;
                                }
                                for (String a : separated){
                                    a = a.replace(" ","");
                                    newAcs[aCount] = a;
                                    aCount++;
                                }
                                actions = newAcs;
                            }
                            else{
                                String[] newAcs = new String[actions.length+1];
                                int aCount = 0;
                                for(String a : actions){
                                    newAcs[aCount] = a;
                                    aCount++;
                                }
                                newAcs[aCount] = acString;
                                actions = newAcs;
                            }
                        }
                    }
                    ScpComponent c = new ScpComponent(name, "Activity", pols, perms, actions);
                    list.add(c);
                }


            for (ServiceInfo si : appServs) {
                String name = si.name;

                String[] perms = appPerms;
                String[] pols = new String[0];
                String[] actions = new String[0];
                String servDeclPerm = si.permission;
                if (servDeclPerm != null){
                    if (!servDeclPerm.contains("scp")) {
                        String policy = "D{[" + servDeclPerm + "]}";
                        String[] temp = new String[pols.length + 1];

                        for (int count = 0; count < pols.length; count++) {
                            temp[count] = pols[count];
                        }
                        temp[pols.length] = policy;
                        pols = temp;
                    }
                }
                if (si.metaData != null) {
                    if ((String) si.metaData.get("SCP-POLS") != null) {
                        String polsString = (String) si.metaData.get("SCP-POLS");
                        if (polsString.contains(";")) {
                            String[] separated = polsString.split(";");
                            String[] newPols = new String[pols.length+separated.length];
                            int pCount = 0;
                            for(String p : perms){
                                newPols[pCount] = p;
                                pCount++;
                            }
                            for (String p : separated){
                                p = p.replace(" ","");
                                newPols[pCount] = p;
                                pCount++;
                            }
                            perms = newPols;
                        }
                        else{
                            String[] newPols = new String[perms.length+1];
                            int pCount = 0;
                            for(String p : perms){
                                newPols[pCount] = p;
                                pCount++;
                            }
                            newPols[pCount] = polsString;
                            perms = newPols;
                        }
                    }
                    if ((String) si.metaData.get("SCP-PERMS") != null) {
                        String permString = (String) si.metaData.get("SCP-PERMS");
                        if (permString.contains(";")) {
                            String[] separated = permString.split(";");
                            String[] newPerms = new String[perms.length+separated.length];
                            int pCount = 0;
                            for(String p : perms){
                                newPerms[pCount] = p;
                                pCount++;
                            }
                            for (String p : separated){
                                p = p.replace(" ","");
                                newPerms[pCount] = p;
                                pCount++;
                            }
                            perms = newPerms;
                        }
                        else{
                            String[] newPerms = new String[perms.length+1];
                            int pCount = 0;
                            for(String p : perms){
                                newPerms[pCount] = p;
                                pCount++;
                            }
                            newPerms[pCount] = permString;
                            perms = newPerms;
                        }

                    }
                    if ((String) si.metaData.get("SCP-ACTIONS") != null) {
                        String acString = (String) si.metaData.get("SCP-ACTIONS");
                        if (acString.contains(";")) {
                            String[] separated = acString.split(";");
                            String[] newAcs = new String[actions.length+separated.length];
                            int aCount = 0;
                            for(String p : actions){
                                newAcs[aCount] = p;
                                aCount++;
                            }
                            for (String a : separated){
                                a = a.replace(" ","");
                                newAcs[aCount] = a;
                                aCount++;
                            }
                            actions = newAcs;
                        }
                        else{
                            String[] newAcs = new String[actions.length+1];
                            int aCount = 0;
                            for(String a : actions){
                                newAcs[aCount] = a;
                                aCount++;
                            }
                            newAcs[aCount] = acString;
                            actions = newAcs;
                        }
                    }
                }
                ScpComponent c = new ScpComponent(name, "Service", pols, perms, actions);
                list.add(c);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


}
