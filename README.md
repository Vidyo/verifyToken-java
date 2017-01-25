# verifyToken-java

Use this to verify tokens that you have generated.

To build, run:

    ./gradlew clean build
    
This will build *verifyToken.jar* in directory build/libs
    
Verify tokens interactively:

    java -jar verifyToken.jar 
    Enter token: cHJvdmlzaW9uAHVzZXIxQEFwcGxpY2F0aW9uSUQANjM2NTI2MDI3ODUAAGQzMDkxMjA5NjFmMGYxMjFkM2FlZjQxMzJkNmRiNTdkMTA5MDU0MGU4ZWZmNjYxMzlhOTUyMzJiODA0MGViOWU5MjI3OTQ3N2MwYWUzODQ3Y2NiYmJiYTNhZDc5OTdkOA==
    Payload: provision\0user1@ApplicationID\063652602785\0
    Expires: Wed Jan 25 17:33:05 EST 2017 | 9352 seconds left (OK)
    Verify signature (y/N): y
    Developer key: rUlaMASgt1Byi4Kp3sKYDeQzo
    Signature: VERIFIED
