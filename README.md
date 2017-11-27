# Gamified Office
Gamified Office, also known as GummyFoo (why not?!) is an application that is built to promote a healthy working environment by translating daily activities such as drinking water and standing to scores. Its frontend is a React-native based android application, which can be found in the RNGamifiedOffice repository. Its backend is located in this very repository.

To run this application, first deploy the backend RESTful service according to the instructions below:

# Scripts
Before deploying the backend service, make sure scripts/setup.sh is properly executed:
```
source setup.sh
```

# Executables
After running the scripts, compile a executable fat jar with Maven
```
mvn clean package
```

Then copy the fat jar to the instance the RESTful service will be run, then:
```
java -jar yourFatJarName-1.0.jar
```

# APK
Once the backend service is deployed, compile an APK from the frontend repository mentioend in the previous section. Make sure you change the host address in the constants/Common.js before you compile.

# Finally
Use the application near a beacon.
