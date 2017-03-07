#!/bin/sh
rm -f *.pem *.p12 *.jks

openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365
openssl pkcs12 -export -out keystore.p12 -inkey key.pem -in cert.pem

keytool -importkeystore -destkeystore keystore.jks -srcstoretype PKCS12 -srckeystore keystore.p12
keytool -list -keystore keystore.jks

keytool -importcert -trustcacerts -file cert.pem -keystore chain.jks
