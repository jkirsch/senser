# senser

Demo Project opencv + Flink streaming

To start the project, run

## first build

    ./mvnw compile -P generate-frontend

## start

    ./mvnw spring-boot:run

  starts `edu.tuberlin.senser.images.MainApp`

Visit <http://localhost:8080/>

## Dependencies

- You should have git on the path
- Maven is self contained using the wrapper accessible from mvnw

### How does it work

The app is orchestrated using Spring for dependency injection. `edu.tuberlin.senser.images.MainApp` is the main entry point.
It uses classpath scanning to find components to initialize within the same package or underneath.

The scanning finds [edu.tuberlin.senser.images.facedetection.video.FaceRecognizerInVideo](src/main/java/edu/tuberlin/senser/images/facedetection/video/FaceRecognizerInVideo.java#L108) which starts the face detection.
It also gets via dependency injection a link to a Person Service, which is connected to an in memory database.

It reads from a video or video stream by opening the resource specified under the name `senser.videosource` in `application.properties`. 
This can be a remote resource with streaming video, or a locally stored video file.

Once a face is found, we try to recognize it, by asking the `lbphFaceRecognizer`

```java
int[] plabel = new int[1];
double[] pconfidence = new double[1];

lbphFaceRecognizer.predict(face_resized, plabel, pconfidence);
LOG.info("Prediction confidence {}", pconfidence[0]);
```

If the confidence is sufficiently high, we know we have a known face.

For simplicity we just add each picture to the training samples, by adding it to the database

```java
box_text = personService.registerImage(personID, face_resized, counter, confidence);
```

and updating the recognizer with the newly found face

```java
lbphFaceRecognizer.update(images, label);
```

Since we store images into the database, we can also inspect them live.
For this end a web controller is exposed as a component, `edu.tuberlin.senser.images.web.controller.ImageController` which
listens on localhost:8080/images/{id}

Just visit <https://localhost:8080/images> to view a global view of all images.

So if there is a person with ID 1 found we can quickly retrieve all training samples using <https://localhost:8080/images/1> .
This will hit the controller, which retrieves the Person Object from the database, sets a model and forwards it to a view "faces"

```java
model.addAttribute("person", personRepository.findOne(id));
return "faces";
```

`faces` is a view in the template cache, found in the resources section `templates/faces.html`, which uses [Thymeleaf](http://www.thymeleaf.org) (similar to jsp) to
dynamically render the resulting page on the server.

```html
<tr th:each="image : ${person.images}">
    <img th:src="*{'data:image/jpg;base64,'+image.getAsBase64()}" alt="Person" />
</tr>
```

To start the flink streaming, the following is used `StreamExample.startFlinkStream();`


## To just start the Face detection run

* edu.tuberlin.senser.images.facedetection.video.WatchTV

This runs face detection on a live video feed.

## IDE Note

If you are running on a 64 bit OS, make sure you start your IDE in 64 bit mode as well, otherwise the dependencies might not resolve correctly.

To determine the correct opencv platform bindings, the `os-maven-plugin` is used.
For integration issues with your IDE, check [Issues with eclipse m2e or other ides](https://github.com/trustin/os-maven-plugin#issues-with-eclipse-m2e-or-other-ides).


