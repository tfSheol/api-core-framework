#!/bin/sh
rm -f *.pem *.p12 *.jks

# Old
# openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365
# openssl pkcs12 -export -out keystore.p12 -inkey key.pem -in cert.pem
# keytool -importkeystore -destkeystore keystore.jks -srcstoretype PKCS12 -srckeystore keystore.p12
# keytool -list -keystore keystore.jks
# keytool -importcert -trustcacerts -file cert.pem -keystore chain.jks


#letsencrypt-auto certonly --standalone -d DOMAIN.TLD -d DOMAIN_2.TLD --email EMAIL@EMAIL.TLD
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name Sheol
keytool -importkeystore -deststorepass test1234 -destkeypass test1234 -destkeystore keystore.jks -srckeystore keystore.p12 -srcstoretype PKCS12 -srcstorepass test1234 -alias Sheol
keytool -list -keystore keystore.jks
keytool -importcert -trustcacerts -file cert.pem -keystore chain.jks