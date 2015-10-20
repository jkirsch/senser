# senser

Demo Project opencv + Flink streaming

To start the project, run

## first build

    mvn compile -P generate-frontend

## start

    mvn spring-boot:run

  starts `edu.tuberlin.senser.images.MainApp`

Visit <http://localhost:8080/>

### How does it work

The app is arhectrated using Spring for dependency injection. `edu.tuberlin.senser.images.MainApp` is the main entry point.
It uses classpath scanning to find components to initialize within the same package or underneath.


The scanning finds `edu.tuberlin.senser.images.facedetection.video.FaceRecognizerInVideo` which starts the face detection.
It also gets via dependency injection a link to a Person Service, which is connected to an in memory database.

Once a face is found, we try to recognize it, by asking the lbphFaceRecognizer

    int[] plabel = new int[1];
    double[] pconfidence = new double[1];

    lbphFaceRecognizer.predict(face_resized, plabel, pconfidence);
    LOG.info("Prediction confidence {}", pconfidence[0]);


If the confidence is sufficiently high, we know we have a known face.

For simplicity we just add each picture to the training samples, by adding it to the database

    box_text = personService.registerImage(personID, face_resized, counter, confidence);

and updating the recognizer with the newly found face

    lbphFaceRecognizer.update(images, label);


Since we store images into the database, we can also inspect them live.
For this end a web controller is exposed as a component, `edu.tuberlin.senser.images.web.controller.ImageController` which
listens on localhost:8080/images/{id}

So if there is a persons with ID 1 found we can quickly retrieve all training samples using <https://localhost:8080/images/1> .
This will hit the controller, which retrieves the Person Object from the database, sets a model and forwards it to a view "faces"

    model.addAttribute("person", personRepository.findOne(id));
    return "faces";

`faces` is a view in the template cache, found in the resources section `templates/faces.html`, which uses thypeleaf (similar to jsp) to
dynamically render the resulting page on the server.

    <tr th:each="image : ${person.images}">
        <img th:src="*{'data:image/jpg;base64,'+image.getAsBase64()}" alt="Person" />
    </tr>


To start the flink streaming, the following is used `StreamExample.startFlinkStream();`


## To just start the Face detection rnu

* edu.tuberlin.senser.images.facedetection.video.WatchTV

This runs face retection on live video feed.

For twitter, set `twitter.enabled=true`

provide credentials in

`src/main/resources/twittersource.properties`

    twitter.consumerKey=
    twitter.consumerSecret=
    twitter.token=
    twitter.secret=

**:warning: !! Don't check this file in !! :warning:**

select keywords in

`src/main/resources/application.propertie`
