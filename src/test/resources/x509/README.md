### Self-signed Unit Certificate

x509 Certificates for testing are generated with below commands.

#### unit.key 

```bash
openssl genrsa -out unit.key
```

#### unit.csr

```bash
openssl req -new -key unit.key -out unit.csr
```

Output( Common Name is set as `localhost` )

```
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:
State or Province Name (full name) [Some-State]:
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:localhost
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```

#### unit-self-sign.crt

```bash
openssl x509 -req -days 3650 -signkey unit.key -out unit-self-sign.crt <unit.csr
```
