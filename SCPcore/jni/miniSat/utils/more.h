#define 	INT32_MAX   0x7fffffffL
#define 	INT32_MIN   (-INT32_MAX - 1L)
#define 	INT64_MAX   0x7fffffffffffffffLL
#define 	INT64_MIN   (-INT64_MAX - 1LL)
#define 	UINT32_MAX  0xffffffff  /* 4294967295U */

  #undef __PRI_8_LENGTH_MODIFIER__
  #undef __PRI_64_LENGTH_MODIFIER__
  #undef __SCN_8_LENGTH_MODIFIER__
  #undef __SCN_64_LENGTH_MODIFIER__

  #if defined(__STDC_LIBRARY_SUPPORTED__)
    #define __PRI_8_LENGTH_MODIFIER__ "hh"
    #define __PRI_64_LENGTH_MODIFIER__ "ll"
    #define __SCN_8_LENGTH_MODIFIER__ "hh"
    #define __SCN_64_LENGTH_MODIFIER__ "ll"
  #else
    #define __PRI_8_LENGTH_MODIFIER__ ""  /* none */
    #define __PRI_64_LENGTH_MODIFIER__ "q"
  #endif

#define PRIi64        __PRI_64_LENGTH_MODIFIER__ "i"
#define PRIu64        __PRI_64_LENGTH_MODIFIER__ "u"
