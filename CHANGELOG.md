## 1.6.0
IMPROVEMENTS:
* Supporting java 17. [#156](https://github.com/personium/personium-engine/pull/156), [#159](https://github.com/personium/personium-engine/pull/159)

NEW FEATURE:
* Implement `_p.installBox` for installing bar in engine script. [#149](https://github.com/personium/personium-engine/pull/149)

MISC:
* Modify test codes. [#151](https://github.com/personium/personium-engine/pull/151)

## 1.5.28
IMPROVEMENTS:
* Implement dynamic path for engine script [#130](https://github.com/personium/personium-engine/pull/130)

BUG FIXES:
* Tests do not work properly. [#14](https://github.com/personium/personium-engine/issues/14)

MISC:
* Add Dockerfile and update README.md [#140](https://github.com/personium/personium-engine/pull/140)

## 1.5.27
IMPROVEMENTS:
* Upgrade personium-lib-common to 1.5.3

## 1.5.26
NEW FEATURE:
* Added attributes to get box name and schema URL  [#111](https://github.com/personium/personium-engine/issues/111)

IMPROVEMENTS:
* Upgrade personium-lib-common to 1.5.2

## 1.5.25
IMPROVEMENTS:
* Upgrade personium-lib-common to 1.5.1

## 1.5.24
IMPROVEMENTS:
* Upgrade personium-lib-common to 1.5.0 [#99](https://github.com/personium/personium-engine/issues/99)
* Add logging of unknown exception's stacktrace. [#102](https://github.com/personium/personium-engine/issues/102)

## 1.5.23
IMPROVEMENTS:
* Update Rhino version to 1.7.11 ([#93](https://github.com/personium/personium-engine/pull/93))

BUG FIXES:
* Correct to close properly.([#94](https://github.com/personium/personium-engine/issues/94))

## 1.5.22
IMPROVEMENTS:
* Set Rhino language version `VERSION_ES6` ([#89](https://github.com/personium/personium-engine/pull/89))
* Add recursive delete support to engine ([#88](https://github.com/personium/personium-engine/pull/88))

## 1.5.20
IMPROVEMENTS:
* Tests do not work properly.([#14](https://github.com/personium/personium-engine/issues/14))
  * Only a part was fixed.(ScriptTest)

BUG FIXES:
* Correct to close properly.([#94](https://github.com/personium/personium-engine/issues/94))

## 1.5.19
BUG FIXES:
* HttpClient close leak.([#77](https://github.com/personium/personium-engine/issues/77))

IMPROVEMENTS:
* Cache compiled script objects.([#74](https://github.com/personium/personium-engine/issues/74))

## 1.5.18
IMPROVEMENTS:
* Maintenance of checkstyle and PMD.([#66](https://github.com/personium/personium-engine/issues/66))

## 1.5.17
BUG FIXES:
* An error occurs when reading the TranscellToken created by engine script with core.([#68](https://github.com/personium/personium-engine/issues/68))

## 1.5.16
BUG FIXES:
* Wrong BOX URL error.([#61](https://github.com/personium/personium-engine/issues/61))
* getFileList(), getColList() does not work.([#64](https://github.com/personium/personium-engine/issues/64))

## 1.5.15
IMPROVEMENTS:
* Support per-Cell FQDN.([#57](https://github.com/personium/personium-engine/issues/57))

## 1.5.14
IMPROVEMENTS:
* Update personium-lib-common version.

## 1.5.13
IMPROVEMENTS:
* Update personium-lib-common version.

## 1.5.12
IMPROVEMENTS:
* Corresponds to authorization code grant flow.([#53](https://github.com/personium/personium-engine/issues/53))
  * Only a part of the function is implemented. Refer to https://github.com/personium/personium-core/issues/213 for details.

## 1.5.11
IMPROVEMENTS:
* Update pom.xml.([#51](https://github.com/personium/personium-engine/issues/51))

## 1.5.10
BUG FIXES:
* ACE list does not return proper role name when "href" is not found.([#50](https://github.com/personium/personium-engine/issues/50))

## 1.5.9
IMPROVEMENTS:
* Update Rhino version to 1.7.7.([#40](https://github.com/personium/personium-engine/issues/40))

## 1.5.8
IMPROVEMENTS:
Add new Action 'relay.event' to Rule.

## 1.5.7
BUG FIXES:
* Schema is not set in Token created by serviceSubjcet.([#37](https://github.com/personium/personium-engine/issues/37))

IMPROVEMENTS:
* Add some headers on request to Personium API.([#35](https://github.com/personium/personium-engine/issues/35))

## 1.5.6
IMPROVEMENTS:
* Add new endpoint for executing system scripts.([#30](https://github.com/personium/personium-engine/issues/30))

## 1.5.5
IMPROVEMENTS:
* Upgrade httpclient of engine.([#32](https://github.com/personium/personium-engine/issues/32))

## 1.5.4
IMPROVEMENTS:
* Unify the linefeed code of file to LF.([#28](https://github.com/personium/personium-engine/issues/28))

## 1.5.3
BUG FIXES:
* Can not access to other cell using serviceSubject.([#20](https://github.com/personium/personium-engine/issues/20))
* Can not access to other cell by _p.as({cellUrl:mycell}).cell('othercell').([#21](https://github.com/personium/personium-engine/issues/21))
* DaoException '401,Unauthorized' is thrown at _p.as().cell().getToken().([#24](https://github.com/personium/personium-engine/issues/24))

IMPROVEMENTS:
* Correction with cell export import.([#26](https://github.com/personium/personium-engine/issues/26))

## 1.5.2
IMPROVEMENTS:
* Organize PersoniumUnitConfig.([#18](https://github.com/personium/personium-engine/issues/18))

## 1.5.1
BUG FIXES:
* Engine Service Library can not register ACL, which is caused by incorrect object names.([#12](https://github.com/personium/personium-engine/issues/12))

IMPROVEMENTS:
* WebDAV file encryption.([#13](https://github.com/personium/personium-engine/issues/13))

## 1.5.0
BUG FIXES:
* Unavailability of TLS 1.2.
 ([#3](https://github.com/personium/personium-client-java/issues/3))

##1.4.5

BUG FIXES:
* Fixed about Executing Cell Recursive Delete API does not delete OData in cell. ([#13](https://github.com/personium/personium-core/issues/13))

## 1.4.4

 BUG FIXES:
 - Loaded method name is invalid in `personium-lib.js `.([#5](https://github.com/personium/personium-engine/issues/5)).


## 1.4.3

 BUG FIXES:
 - Remove Checkstyle Errors and PMD warnings.

## 1.4.2

BREAKING CHANGES:

 - Rename package name and some parameters from `com.fujitsu.dc` or `dc` to `io.personium` or `personium` . ([#1](https://github.com/personium/personium-engine/issues/1))

  * Changed parameters' names are below:

 |#  |Name           | V1.4.1 and previous | 1.4.2 or later | Example or Usage |
 |:-:|:--------------|:------------------------|:---------------|:---------|
 | 1 |Unit Configuration file name|dc-config.properties|personium-unit-config.properties||
 | 2 |XML name space/URN|`xmlns:dc='urn:x-dc1:xmlns'` |`xmlns:p='urn:x-personium:xmlns'`| WebDAV Property data, bar file document or Authn API|
 | 3 |HTTP request header|`X-Dc-Xxxxx`|`X-Personium-Xxxxx`| All APIs with original request header |
 | 4 |POST request parameters' key|`dc_xxxxx`|`p_xxxxx`| APIs with post parameters  |
 | 5 |Core APIs for Engine Javascript|`dc.xxxx`|`_p.xxxxx`| Engine service script (server-side logic)|


## 1.4.1

IMPROVEMENTS:
  - core *[UriUtils.java, CellEsImpl.java,TokenEndPointResource.java etc]* :<br>
    A new custom URL scheme `personium-localunit` is introduced for more flexible server operation.<br>
    It is now supported in major APIs handling URL.
   * You can write `personium-localunit:/cell1/` instead of `https://myunit.example/cell1/` in fields such as  `Box.Schema` or `ExtCell.Url` when referring within the same Unit.

   * In previous versions, Unit FQDN change requires data conversion in most cases.
   * By using this new URL scheme, links among Cells on a Unit can be kept without data conversion after Unit FQDN change.

BUG FIXES:

  - core *[DcEngineSvcCollectionResource.java etc]*, engine *[FsServiceResourceSourceManager.java etc]*: <br>
    The script file could not be loaded for enabling custom API was fixed. (Issue #27)

  - core *[DavCmpFsImpl.java]*:<br>
    The bug that the content length is always reset to 0 at updating WebDAV file was fixed.(Issue #29)

## 1.4.0

BREAKING CHANGES:
  - core, engine *[dc-config-default.properties]*:<br>

   Changed unit configuration file keys' prefixes from `com.fujitsu.dc.*` to `io.personium.*`. It is necessary for V1.3.X users to replace all keys' prefixes in `dc-config.properties` file.

IMPROVEMENTS:
  - core *[CellCmpFsImpl.java, BoxCmpFsImpl.java, DavCmpFsImpl.java etc]*, es-api-2.4 *[userdata.json etc]*:<br>

   Supports [elasticsearch v2.4.1](https://www.elastic.co/guide/en/elasticsearch/reference/2.4/release-notes-2.4.1.html). <br>
   * The `es-api-2.4` module is newly developed for elasticsearch v2.4.X support.
   * Personium V1.4.0 (or later) must run with es-api-2.4 module.
   * Current es-api-2.4 supports ElasticSearch v2.4.1.
   * For the incompatibilities between elasticsearch v1.3.X and v2.4.X, the registration formats of data (OData / WebDAV) were changed. (Show in details below.)

BACKWARD INCOMPATIBILITIES:
  - core *[CellCmpFsImpl.java, BoxCmpFsImpl.java, DavCmpFsImpl.java etc]*, es-api-2.4 *[userdata.json etc]*:<br>
   For supporting [elasticsearch v2.4.1](https://www.elastic.co/guide/en/elasticsearch/reference/2.4/release-notes-2.4.1.html), the registration formats of inner data (OData / WebDAV) were changed. <br>
   For these incompatibilities, V1.4.0 cannot run with the same data construction in server as V1.3.X. To upgrade version from V1.3.X, inner data must be converted in new rules below.

   * The WebDAV data are stored in file system. <br>
     Stored directory path is set by `dc-config.properties`,  the default path is `/personium_nfs/dc-core/dav`.
   * Elasticsearch mapping definition key `"l"` was replaced to `"ll"` in `UserData` object.<br>
     (The other objects which have `"l.*"` mapping keys are not changed.)


## 1.3.25

IMPROVEMENTS:
  - core *[Common.java, Box.java, ExtCell.java, ODataUtils.java etc]*:<br>
    Improved formats of Box object's `"Schema"` and ExtCell object's `"Name"`. <br>
    The definitions of these formats are below.<br>

|Object  |Property |Format name  |Proper format definition                      |Examples           |
|:-------|:--------|:------------|:---------------------------------------------|:------------------|
|Box     |Schema   |`Schema URI` |`Cell URL` or URN.                            |http://fqdn/adc/<br>urn:x-dc1:adc|
|ExtCell |Name     |`Cell URL`   |Normalized URL with http(s) scheme and **trailing slash**.|http://fqdn/adc/|

   * The formats of URI, URN and URL are based on [RFC3986](https://tools.ietf.org/html/rfc3986).
   * **Trailing slash** is the character `/` which is in the end of URL.
   * These values are validated at the time of creating/updating `Box` or `ExtCell`.
   * If the above check fails, the response code will be `400 Bad Request`.

## 1.3.24

IMPROVEMENTS:
  - core *[pom.xml etc]*:<br>
   Enabled Java 8 compilation.
   * Compilation on Java 8 used to fail due to incompatibility between Java 7 and 8. Now it is fixed and both Java 7 and 8 can compile the source.
   * Also checked it surely runs on Java 8.

  - core *[Account.java, Common.java, AbstractODataResource.java etc]*:<br>
   Fixed the known issue in V1.3.23 about [Create Account API](https://github.com/personium/io/wiki/Account#create).
   * Account object's `"Type"` value can be validated when you create a new Account.
   * Available values are:<br> `"basic"`,`"oidc:google"` or these space-separated values such as `"oidc:google basic"`.
   * If the above check fails, the response code will be `400 Bad Request`.

  - core *[TokenEndPointResource.java etc]*:<br>
   Fixed the bug about password authentication.
   * Added checking that Account object's `"Type"` value contains `"basic"` at the time of password authentication.
   * If the above check fails, the response code will be `400 Bad Request`.

## 1.3.23

IMPROVEMENTS:

  - core *[IdToken.java, TokenEndPointResource.java etc]*:<br>
   Supports [OpenID Connect](http://openid.net/connect/) (OIDC). In V1.3.23, supporting ID provider is **Google** only.
   * When creating OIDC Account, post [Create Account API](https://github.com/personium/io/wiki/Account#create) with  `"Type"` key parameter in the request body such as `{"Name":"[GMAIL_ADDRESS]", "Type":"oidc:google"}`. Account name **must be Gmail address** (ex. `example@gmail.com`) to use Google OIDC. When you create an account which is authenticated with both basic ID/PW and OIDC, separate two values with **space** such as `{"Type":"basic oidc:google"}`. (Without setting of `"Type"` value, the default is `basic`).
   * When utilizing OIDC Authentication, need to set `com.fujitsu.dc.core.oidc.google.trustedClientIds=[CLIENT_ID]` configuration by `dc-confing.properties` file. (You can get client IDs by registering your apps to [Google developer Console](https://console.developers.google.com/home).) If you want to set multiple client IDs, these values must be separated with **space**. You can also configure wild card `*`, but this setting causes more security risks, so we strongly recommend for debugging use only. (Without setting, default value is `*`.)
   * When authenticating an account with OIDC, use [Authentication API](https://github.com/personium/io/wiki/Authentication-and-Authorization) with request body `"grant_type=urn:x-dc1:oidc:google&id_token=[ID_TOKEN]"` . If you need more information with __OAuth2.0 ID Token__, refer to [Google Developers site](https://developers.google.com/identity/protocols/OpenIDConnect).

  - core *[DavDestination.java, DavMoveResource.java, DavCollectionResource.java, DavCmpEsImpl.java, etc.]*:<br>
   * MOVE method([RFC2518](https://tools.ietf.org/html/rfc2518#section-8.9)) for WedDAV collections and stored files are implemented. (Some restrictions apply.)
   * MOVE method requires `Destination:` header which is absolute URI expressing the name or the directory to be changed to. MOVE method can be used by below items:

    1. WebDAV collections.
    2. OData collections.
    3. Service collections.
    4. WebDAV collection files.
    5. Service collection files under `/__src` directory.

   * If you need more information this API, refer to [Collection Move API Documentation](https://github.com/personium/io/wiki/Collection#move).

   #####API examples:

   ######Rename collection (end slash is required)
   ```curl
   curl -X MOVE "http://[FQDN]/[cell]/[box]/[collection]/[old_name]/"
   -H "Destination:http://[FQDN]/[cell]/[box]/[collection]/[new_name]/" -i -k -s
   ```

   ######Rename file
   ```curl
   curl -X MOVE "http://[FQDN]/[cell]/[box]/[collection]/[dir]/old.txt"
   -H "Destination:http://[FQDN]/[cell]/[box]/[collection]/[dir]/new.txt" -i -k -s
   ```

   ######Move file
   ```curl
   curl -X MOVE "http://[FQDN]/[cell]/[box]/[collection]/[from]/file.txt"
   -H "Destination:http://[FQDN]/[cell]/[box]/[collection]/[to]/file.txt" -i -k -s
   ```

  - core *[UserSchemaODataProducer.java ]*:<br>
    PUT methods to change the name of following items are implemented.
   * EntityType
   * Property
   * ComplexTypeProperty
   * AssociationEnd

KNOWN ISSUES:
  - core :
   When [creating new Account](https://github.com/personium/io/wiki/Account#create), the posted `"Type"` value is not validated. New account can be created whatever the `"Type"` value is, such mistaking values as `"Type": "basic oidc:facebook"` (not supported ID Provider), `"Type": "basic_oidc:google"` (separated by underscore) etc.

## 1.3.22a

BACKWARD INCOMPATIBILITIES:

  - core *[DcCoreAuthnException.java]*:
     Response code for authentication failure with OAuth 2.0 (__auth endpoint) has been changed as follows.

    | Versions         | Response code & header      |
    | :-----------     | :-------------------------- |
    | Prior to V1.3.22 | 401 with/without authentication header depending on authentication type. |
    | V1.3.22          | 401 with header "WWW-Authenticate: xxxxx" |
    | From V1.3.22a    | Basic authentication: 400 with header "WWW-Authenticate: Basic".  Client authentication: See KNOWN ISSUES below. |


KNOWN ISSUES:

  - core :
    Response code for client authentication failure with OAuth 2.0 (__auth endpoint) should be 401 and include
    "WWW-Authenticate" response header. However current version of personium.io returns response code 400 without
    authenticate header due to compatibility for existing applications.


## 1.3.22

IMPROVEMENTS:

  - core *[EsQueryHandler.java]*:
    Implemented `ne` (not equal) operator for OData $filter query. List of supported operators and functions follows.

    | Operator | Description           | Example                                                                  | Note |
    | :------- | :-------------------  | :----------------------------------------------------------------------- | :--- |
    | eq       | Equal                 |  \$filter=itemKey eq 'searchValue'  <br/> \$filter=itemkey eq 10         |      |
    | ne       | Not equal             | $filter=itemKey ne 'searchValue'                                         |      |
    | gt       | Greater than          | $filter=itemKey gt 1000                                                  |      |
    | ge       | Greater than or equal | $filter=itemKey ge 1000                                                  |      |
    | lt       | Less than             | $filter=itemKey lt 1000                                                  |      |
    | le       | Less than or equal    | $filter=itemKey le 1000                                                  |      |
    | gt       | Greater than          | $filter=itemKey gt 1000                                                  |      |
    | and      | Logical and           | $filter=itemKey eq 'searchValue1' and itemKey2 eq 'searchValue2'         |      |
    | or       | Logical or            | $filter=itemKey eq 'searchValue1' or itemKey2 eq 'searchValue2'          |      |
    | ()       | Precedence grouping   | $filter=itemKey eq 'searchValue' or (itemKey gt 500 and itemKey lt 1500) |      |

    | Function    | Description        | Example                                       | Note                         |
    | :---------- | :----------------- | :-------------------------------------------- | :--------------------------- |
    | startswith  |                    | $filter=startswith(itemKey, 'searchValue')    | Null value is not supported. |
    | substringof |                    | $filter=substringof('searchValue1', itemKey1) | Null value is not supported. |

BUG FIXES:

  - core *[EsQueryHandler.java, DcOptionsQueryParser.java, DcCoreExceptoin.java]*:
    Unexpected result or error was retunred when unsupported operator or function is specified in query. Now returns Bad request (400).

  - core *[EsQueryHandler.java]*:
    No data was returned when searching with query that contains control codes as an operand. Fixed.

BACKWARD INCOMPATIBILITIES:

  - core *[EsQueryHandler.java, DcOptionsQueryParser.java, FilterConditionValidator.java, DcCoreExceptoin.java]*:
    Due to the above improvements and bug fixes, $filter behavior has been changed as follows:

    || When undefined property is specified as query operand. |
    |:--- |:----|
    | Prior to V1.3.22 | Nothing is Returned. |
    | From V1.3.22     | Bad Request(400) |

    || When the format of operand value is different from the type of property. |
    |:--- |:----|
    | Prior to V1.3.22 | If the operand value is castable to the type of assocaiated property, the operand is treated as valid.<br/>If not castable, retunrs Bad Request(400).  |
    | From V1.3.22     | Bad Request(400) |

    || When operand value is out of range for the type of property.|
    |:--- |:---- |
    | Prior to V1.3.22 | The operand value is treated as a valid operand, but may cause either unexpected result or error.|
    | From V1.3.22     | Bad Request(400) |

    || To search data including \\ (back-slash) |
    |:--- |:---- |
    | Prior to V1.3.22 | No escaping is required in query value.. |
    | From V1.3.22     | Escaping '\' (back-slash) required, such as '\\\\' |




## 1.3.21

BREAKING CHANGES:

  - core *[AccessConetxt.java, etc.]*:
    Supports Basic authentication (user, password) with Authorization header to allow access to the resource.

BACKWARD INCOMPATIBILITIES:

  - core *[BoxPrivilege.java, ODataSvcCollectionResource.java, ODataSvcSchemaResource.java]*:
    Added `alter-schema` privilege.  Prior to 1.3.21, OData schema can be changed with `write` privilege,  but from 1.3.21, `alter-schema` privilege is required to change the schema.

  - core *[BoxUrlResource.java]*:
    Changed response code of "Get Box URL" API from 302 to 200 to prevent redirection to the "Location URL" on some environment.

IMPROVEMENTS:

  - core *[DcCoreConfig.java, AuthUtils.java]*:
    Password salt was hard-coded and the same value was used for every personium runtime,  so that it could be a threat in terms of security. Now it can be specified with individual value in dc-config.properties.

  - core *[BinaryDataAccessor.java]*:
    Corrected file write operation to ensure that the data is flushed and synced to the storage device.

BUG FIXES:

  - core *[DcEngineSvcCollectionResource.java]*, engine *[PersoniumResponse.java]*:
    Status code 500 was returned when "Transfer-Encoding: chuncked" header was given on engine response. Fixed.

  - core *[AccessContext.java, DcCoreAuthzException.java, etc.]*:
    Authentication and authorization behavior is corrected to comply with HTTP RFC.
