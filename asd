diff --git a/.gradle/buildOutputCleanup/buildOutputCleanup.lock b/.gradle/buildOutputCleanup/buildOutputCleanup.lock
index 2a2b780..de0693f 100644
Binary files a/.gradle/buildOutputCleanup/buildOutputCleanup.lock and b/.gradle/buildOutputCleanup/buildOutputCleanup.lock differ
diff --git a/.gradle/buildOutputCleanup/outputFiles.bin b/.gradle/buildOutputCleanup/outputFiles.bin
index 5f6199a..231ad9b 100644
Binary files a/.gradle/buildOutputCleanup/outputFiles.bin and b/.gradle/buildOutputCleanup/outputFiles.bin differ
diff --git a/.gradle/checksums/checksums.lock b/.gradle/checksums/checksums.lock
index b3da550..3f7c687 100644
Binary files a/.gradle/checksums/checksums.lock and b/.gradle/checksums/checksums.lock differ
diff --git a/.idea/compiler.xml b/.idea/compiler.xml
index f0410af..536a59a 100644
--- a/.idea/compiler.xml
+++ b/.idea/compiler.xml
@@ -8,7 +8,7 @@
         <processorPath useClasspath="false">
           <entry name="$USER_HOME$/.gradle/caches/modules-2/files-2.1/org.projectlombok/lombok/1.18.12/48e4e5d60309ebd833bc528dcf77668eab3cd72c/lombok-1.18.12.jar" />
         </processorPath>
-        <module name="greenFuxes_main" />
+        <module name="greenFuxes.main" />
       </profile>
     </annotationProcessing>
     <bytecodeTargetLevel target="1.8" />
diff --git a/Dockerfile b/Dockerfile
deleted file mode 100644
index 27d4f21..0000000
--- a/Dockerfile
+++ /dev/null
@@ -1,12 +0,0 @@
-FROM gradle:6.6.1-jdk11 AS build
-COPY --chown=gradle:gradle . /home/gradle/src
-WORKDIR /home/gradle/src
-RUN ./gradlew bootjar
-
-FROM openjdk:11.0.4-jre-slim
-
-EXPOSE 8080
-
-COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
-
-ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
\ No newline at end of file
diff --git a/Jenkinsfile b/Jenkinsfile
deleted file mode 100644
index 223e049..0000000
--- a/Jenkinsfile
+++ /dev/null
@@ -1,62 +0,0 @@
-// small instance with linux20.04lts = ami-078db6d55a16afc82
-
-pipeline {
-  agent any
-  environment {
-    registry = "hackathon2020-09-greenfuxes"
-    // registryCredential = 'docker-technical-foxyfox'
-    // dockerImage = ''
-    // ENV_NAME = "Devma-dev"
-    // S3_BUCKET = "elasticbeanstalk-eu-west-2-124429370407"
-    // APP_NAME = 'Devma'
-  }
-  stages {
-    // stage('Gradle Build') {
-    //   steps {
-    //     sh './gradlew build --rerun-tasks'
-    //   }
-    // }
-    stage('Deploy docker image') {
-      when{
-          branch 'develop'
-      }
-      steps {
-        script {
-          image = docker.build("benebp/hackathon2020-09-greenfuxes:latest")
-        }
-        script{
-          docker.withRegistry('', "benebp-dockerhub"){
-            image.push()
-          }
-        }
-      }
-    }
-    // stage('Deploy to AWS') {
-    //   when{
-    //       branch 'develop'
-    //   }
-    //   steps {
-    //     withAWS(credentials:'devma-staging ', region: 'eu-west-2') {
-    //       sh 'aws s3 cp ./DevDockerrun.aws.json \
-    //       s3://$S3_BUCKET/$BUILD_ID/Dockerrun.aws.json'
-    //       sh 'aws elasticbeanstalk create-application-version \
-    //       --application-name "$APP_NAME" \
-    //       --version-label devma-Dev-$BUILD_ID \
-    //       --source-bundle S3Bucket="$S3_BUCKET",S3Key="$BUILD_ID/Dockerrun.aws.json" \
-    //       --auto-create-application'
-    //       sh 'aws elasticbeanstalk update-environment \
-    //       --application-name "$APP_NAME" \
-    //       --environment-name $ENV_NAME \
-    //       --version-label devma-Dev-$BUILD_ID'
-    //     }
-    //   }
-    // }
-  }
-  // post{
-  //   always{
-  //     junit 'build/test-results/**/*.xml'
-  //     step([$class: 'JacocoPublisher', execPattern: 'target/*.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', exclusionPattern: 'src/test*'])
-  //   }
-  // }
-}
-
diff --git a/build/resources/main/application.properties b/build/resources/main/application.properties
index a104870..473aa7d 100644
--- a/build/resources/main/application.properties
+++ b/build/resources/main/application.properties
@@ -2,7 +2,7 @@
 spring.datasource.url=${DATASOURCE_URL}
 spring.datasource.username=${DATASOURCE_USERNAME}
 spring.datasource.password=${DATASOURCE_PASSWORD}
-spring.jpa.hibernate.ddl-auto=create-drop
+spring.jpa.hibernate.ddl-auto=update
 spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
 spring.logging.level.org.hibernate.SQL=debug
 spring.jpa.show-sql=true
\ No newline at end of file
diff --git a/src/main/java/com/app/greenFuxes/controller/canteen/CanteenController.java b/src/main/java/com/app/greenFuxes/controller/canteen/CanteenController.java
deleted file mode 100644
index ea5e970..0000000
--- a/src/main/java/com/app/greenFuxes/controller/canteen/CanteenController.java
+++ /dev/null
@@ -1,58 +0,0 @@
-package com.app.greenFuxes.controller.canteen;
-
-import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
-import com.app.greenFuxes.dto.http.HttpResponse;
-import com.app.greenFuxes.entity.user.User;
-import com.app.greenFuxes.exception.user.UserNotFoundException;
-import com.app.greenFuxes.service.canteen.CanteenService;
-import com.app.greenFuxes.service.user.UserService;
-import io.swagger.v3.oas.annotations.parameters.RequestBody;
-import org.springframework.beans.factory.annotation.Autowired;
-import org.springframework.http.HttpStatus;
-import org.springframework.http.ResponseEntity;
-import org.springframework.web.bind.annotation.*;
-
-import java.security.Principal;
-
-@RestController
-@RequestMapping("/canteen")
-public class CanteenController {
-    private CanteenService canteenService;
-    private UserService userService;
-
-    @Autowired
-    public CanteenController(CanteenService canteenService, UserService userService) {
-        this.canteenService = canteenService;
-        this.userService = userService;
-    }
-
-    @PostMapping("/apply")
-    ResponseEntity<?> bookCanteenPlace(@RequestBody Principal userPrincipal) throws UserNotFoundException {
-        return response(HttpStatus.OK, canteenService.lunchUser(findUserByPrincipal(userPrincipal)));
-    }
-
-    @PostMapping("/finish")
-    ResponseEntity<?> finishLunch(@RequestBody Principal userPrincipal) throws UserNotFoundException {
-        canteenService.finishLunch(findUserByPrincipal(userPrincipal));
-        return response(HttpStatus.OK, "Finishing lunch was successful!");
-    }
-
-    @GetMapping("/status")
-    ResponseEntity<?> getCanteenStatus(@RequestBody Principal userPrincipal) throws UserNotFoundException {
-        return new ResponseEntity<>(canteenService.canteenStatus(findUserByPrincipal(userPrincipal)), HttpStatus.OK);
-    }
-
-    @PutMapping("/configure")
-    ResponseEntity<?> configureCanteen(@RequestBody CanteenSettingDTO canteenSettingDTO, Principal userPrincipal) throws UserNotFoundException {
-        canteenService.configureCanteen(findUserByPrincipal(userPrincipal), canteenSettingDTO);
-        return response(HttpStatus.OK, "Configuration was successful!");
-    }
-
-    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String msg) {
-        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), msg.toUpperCase()), httpStatus);
-    }
-
-    private User findUserByPrincipal(Principal userPrincipal) throws UserNotFoundException {
-        return userService.findByUsername(userPrincipal.getName());
-    }
-}
diff --git a/src/main/java/com/app/greenFuxes/controller/office/OfficeController.java b/src/main/java/com/app/greenFuxes/controller/office/OfficeController.java
deleted file mode 100644
index cc302fd..0000000
--- a/src/main/java/com/app/greenFuxes/controller/office/OfficeController.java
+++ /dev/null
@@ -1,30 +0,0 @@
-package com.app.greenFuxes.controller.office;
-
-import com.app.greenFuxes.service.canteen.CanteenService;
-import com.app.greenFuxes.service.office.OfficeService;
-import org.springframework.beans.factory.annotation.Autowired;
-import org.springframework.http.HttpStatus;
-import org.springframework.http.ResponseEntity;
-import org.springframework.web.bind.annotation.PostMapping;
-import org.springframework.web.bind.annotation.RequestMapping;
-import org.springframework.web.bind.annotation.RestController;
-
-@RestController
-@RequestMapping(path = "/office")
-public class OfficeController {
-
-  private OfficeService officeService;
-  private CanteenService canteenService;
-
-  @Autowired
-  public OfficeController(OfficeService officeService, CanteenService canteenService) {
-    this.officeService = officeService;
-    this.canteenService = canteenService;
-  }
-
-  @PostMapping("/create")
-  public ResponseEntity<?> createOffice() throws Exception {
-    canteenService.addCanteen(officeService.create().getId());
-    return new ResponseEntity<>(HttpStatus.OK);
-  }
-}
diff --git a/src/main/java/com/app/greenFuxes/controller/office/OfficeStatusController.java b/src/main/java/com/app/greenFuxes/controller/office/OfficeStatusController.java
deleted file mode 100644
index 32b6bc8..0000000
--- a/src/main/java/com/app/greenFuxes/controller/office/OfficeStatusController.java
+++ /dev/null
@@ -1,29 +0,0 @@
-package com.app.greenFuxes.controller.office;
-
-import com.app.greenFuxes.dto.office.CapacityDTO;
-import com.app.greenFuxes.service.office.OfficeService;
-import org.springframework.beans.factory.annotation.Autowired;
-import org.springframework.http.HttpStatus;
-import org.springframework.http.ResponseEntity;
-import org.springframework.web.bind.annotation.PostMapping;
-import org.springframework.web.bind.annotation.RequestBody;
-import org.springframework.web.bind.annotation.RequestMapping;
-import org.springframework.web.bind.annotation.RestController;
-
-@RestController
-@RequestMapping(path = "/office-status")
-public class OfficeStatusController {
-
-  private final OfficeService officeService;
-
-  @Autowired
-  public OfficeStatusController(OfficeService officeService) {
-    this.officeService = officeService;
-  }
-
-  @PostMapping("/set-headcount")
-  public ResponseEntity<?> setCapacity(@RequestBody CapacityDTO capacityDTO) throws Exception {
-    officeService.setHeadCount(capacityDTO.getCapacity());
-    return new ResponseEntity<>(HttpStatus.OK);
-  }
-}
diff --git a/src/main/java/com/app/greenFuxes/dto/canteen/CanteenSettingDTO.java b/src/main/java/com/app/greenFuxes/dto/canteen/CanteenSettingDTO.java
deleted file mode 100644
index f64e78a..0000000
--- a/src/main/java/com/app/greenFuxes/dto/canteen/CanteenSettingDTO.java
+++ /dev/null
@@ -1,15 +0,0 @@
-package com.app.greenFuxes.dto.canteen;
-
-import lombok.AllArgsConstructor;
-import lombok.Getter;
-import lombok.NoArgsConstructor;
-import lombok.Setter;
-
-@Getter
-@Setter
-@NoArgsConstructor
-@AllArgsConstructor
-public class CanteenSettingDTO {
-    private Integer maxCanteenCapacity;
-    private Integer lunchtimeInMinute;
-}
diff --git a/src/main/java/com/app/greenFuxes/dto/canteen/CanteenStatusDTO.java b/src/main/java/com/app/greenFuxes/dto/canteen/CanteenStatusDTO.java
deleted file mode 100644
index ad9b172..0000000
--- a/src/main/java/com/app/greenFuxes/dto/canteen/CanteenStatusDTO.java
+++ /dev/null
@@ -1,27 +0,0 @@
-package com.app.greenFuxes.dto.canteen;
-
-import com.app.greenFuxes.entity.canteen.Canteen;
-import com.app.greenFuxes.entity.user.User;
-import lombok.AllArgsConstructor;
-import lombok.Getter;
-import lombok.NoArgsConstructor;
-import lombok.Setter;
-
-import java.util.ArrayList;
-
-@Getter
-@Setter
-@NoArgsConstructor
-@AllArgsConstructor
-public class CanteenStatusDTO {
-
-    private Integer freeSpace;
-    private ArrayList<User> usersInCanteen;
-    private ArrayList<User> userQueue = new ArrayList<>();
-
-    public CanteenStatusDTO(Canteen canteen) {
-        this.freeSpace = canteen.getUsersInCanteen().remainingCapacity();
-        this.usersInCanteen = new ArrayList<>(canteen.getUsersInCanteen());
-        this.userQueue = new ArrayList<>(canteen.getUserQueue());
-    }
-}
diff --git a/src/main/java/com/app/greenFuxes/dto/office/CapacityDTO.java b/src/main/java/com/app/greenFuxes/dto/office/CapacityDTO.java
deleted file mode 100644
index 535ae69..0000000
--- a/src/main/java/com/app/greenFuxes/dto/office/CapacityDTO.java
+++ /dev/null
@@ -1,15 +0,0 @@
-package com.app.greenFuxes.dto.office;
-
-import lombok.AllArgsConstructor;
-import lombok.Getter;
-import lombok.NoArgsConstructor;
-import lombok.Setter;
-
-@Getter
-@Setter
-@NoArgsConstructor
-@AllArgsConstructor
-public class CapacityDTO {
-
-  private int capacity;
-}
diff --git a/src/main/java/com/app/greenFuxes/entity/canteen/Canteen.java b/src/main/java/com/app/greenFuxes/entity/canteen/Canteen.java
deleted file mode 100644
index 82dd0af..0000000
--- a/src/main/java/com/app/greenFuxes/entity/canteen/Canteen.java
+++ /dev/null
@@ -1,66 +0,0 @@
-package com.app.greenFuxes.entity.canteen;
-
-import com.app.greenFuxes.entity.user.User;
-import lombok.AllArgsConstructor;
-import lombok.Getter;
-import lombok.NoArgsConstructor;
-import lombok.Setter;
-
-import java.util.*;
-import java.util.concurrent.LinkedBlockingQueue;
-
-@Getter
-@Setter
-@NoArgsConstructor
-@AllArgsConstructor
-public class Canteen {
-
-    private Long officeId;
-    private Integer maxCanteenCapacity = 10;
-    private Integer lunchtimeInMinute = 30;
-    private LinkedBlockingQueue<User> usersInCanteen = new LinkedBlockingQueue<>(this.maxCanteenCapacity);
-    private Queue<User> userQueue = new LinkedList<>();
-    private Map<User, Date> lunchStarted = new HashMap<>();
-
-    public Canteen(Long officeId) {
-        this.officeId = officeId;
-    }
-
-    public boolean lunchUser(User user) {
-        if (this.usersInCanteen.offer(user)) {
-            this.lunchStarted.put(user, new Date(System.currentTimeMillis()));
-            return true;
-        } else {
-            this.userQueue.add(user);
-            return false;
-        }
-    }
-
-    public User finishLunch(User user) {
-        this.usersInCanteen.remove(user);
-        this.lunchStarted.remove(user);
-        User nextUser = this.userQueue.poll();
-        if (nextUser != null) {
-            this.usersInCanteen.add(nextUser);
-            this.lunchStarted.put(user, new Date(System.currentTimeMillis()));
-            return nextUser;
-        } else {
-            return null;
-        }
-
-    }
-
-    public void restartDay() {
-        this.usersInCanteen = new LinkedBlockingQueue<>(this.maxCanteenCapacity);
-        this.userQueue = new LinkedList<>();
-        this.lunchStarted = new HashMap<>();
-    }
-
-    public void kickGreedy() {
-        for (Map.Entry<User, Date> user : this.lunchStarted.entrySet()) {
-            if (new Date(System.currentTimeMillis()).getTime() / 100000 - user.getValue().getTime() / 100000 > this.lunchtimeInMinute) {
-                finishLunch(user.getKey());
-            }
-        }
-    }
-}
diff --git a/src/main/java/com/app/greenFuxes/entity/office/Office.java b/src/main/java/com/app/greenFuxes/entity/office/Office.java
index a194b57..141b713 100644
--- a/src/main/java/com/app/greenFuxes/entity/office/Office.java
+++ b/src/main/java/com/app/greenFuxes/entity/office/Office.java
@@ -1,24 +1,25 @@
 package com.app.greenFuxes.entity.office;
 
 import com.app.greenFuxes.entity.reservedDate.ReservedDate;
+import java.util.List;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
 import lombok.Getter;
-import lombok.NoArgsConstructor;
 import lombok.Setter;
 
-import javax.persistence.*;
-import java.util.List;
-
 @Entity
 @Getter
 @Setter
-@NoArgsConstructor
 public class Office {
 
-    @Id
-    @GeneratedValue(strategy = GenerationType.IDENTITY)
-    private Long id;
-    private int capacity;
+  @Id
+  @GeneratedValue(strategy = GenerationType.IDENTITY)
+  private Long id;
+  private Long capacity;
 
-    @OneToMany(mappedBy = "office")
-    private List<ReservedDate> reservedDates;
+  @OneToMany(mappedBy = "office")
+  private List<ReservedDate> reservedDates;
 }
diff --git a/src/main/java/com/app/greenFuxes/entity/user/ConfirmationToken.java b/src/main/java/com/app/greenFuxes/entity/user/ConfirmationToken.java
index ec04922..d088459 100644
--- a/src/main/java/com/app/greenFuxes/entity/user/ConfirmationToken.java
+++ b/src/main/java/com/app/greenFuxes/entity/user/ConfirmationToken.java
@@ -16,10 +16,10 @@ import java.util.UUID;
 public class ConfirmationToken {
     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
-    @Column(name="token_id", length = 250)
+    @Column(name="token_id")
     private Long tokenid;
 
-    @Column(name="confirmation_token", length = 250)
+    @Column(name="confirmation_token")
     private String confirmationToken;
 
     @Temporal(TemporalType.TIMESTAMP)
diff --git a/src/main/java/com/app/greenFuxes/entity/user/User.java b/src/main/java/com/app/greenFuxes/entity/user/User.java
index d589043..06987a7 100644
--- a/src/main/java/com/app/greenFuxes/entity/user/User.java
+++ b/src/main/java/com/app/greenFuxes/entity/user/User.java
@@ -1,6 +1,5 @@
 package com.app.greenFuxes.entity.user;
 
-import com.app.greenFuxes.entity.canteen.Canteen;
 import com.app.greenFuxes.entity.reservedDate.ReservedDate;
 import com.fasterxml.jackson.annotation.JsonIgnore;
 import java.io.Serializable;
@@ -27,13 +26,13 @@ public class User implements Serializable {
   @Column(nullable = false, updatable = false)
   private Long id;
 
-  @Column(unique = true, length = 250)
+  @Column(unique = true)
   private String userName;
 
   @JsonIgnore
   private String password;
 
-  @Column(unique = true, length = 250)
+  @Column(unique = true)
   private String email;
 
   private String firstName;
@@ -47,11 +46,10 @@ public class User implements Serializable {
   @ManyToOne
   private ReservedDate reservedDate;
 
-  public User(String userName, String password, String role, String[] authorities, Boolean active, Boolean notLocked) {
+  public User(String userName, String password, String role, Boolean active, Boolean notLocked) {
     this.userName = userName;
     this.password = password;
     this.role = role;
-    this.authorities = authorities;
     this.active = active;
     this.notLocked = notLocked;
   }
diff --git a/src/main/java/com/app/greenFuxes/repository/officeRepository/OfficeRepository.java b/src/main/java/com/app/greenFuxes/repository/officeRepository/OfficeRepository.java
deleted file mode 100644
index c6175f3..0000000
--- a/src/main/java/com/app/greenFuxes/repository/officeRepository/OfficeRepository.java
+++ /dev/null
@@ -1,10 +0,0 @@
-package com.app.greenFuxes.repository.officeRepository;
-
-import com.app.greenFuxes.entity.office.Office;
-import org.springframework.data.repository.CrudRepository;
-import org.springframework.stereotype.Repository;
-
-@Repository
-public interface OfficeRepository extends CrudRepository<Office, Long> {
-
-}
diff --git a/src/main/java/com/app/greenFuxes/service/canteen/CanteenManager.java b/src/main/java/com/app/greenFuxes/service/canteen/CanteenManager.java
deleted file mode 100644
index 64693d4..0000000
--- a/src/main/java/com/app/greenFuxes/service/canteen/CanteenManager.java
+++ /dev/null
@@ -1,30 +0,0 @@
-package com.app.greenFuxes.service.canteen;
-
-import com.app.greenFuxes.entity.canteen.Canteen;
-import lombok.Getter;
-import lombok.Setter;
-
-import java.util.ArrayList;
-
-@Getter
-@Setter
-public class CanteenManager {
-
-    private static CanteenManager instance;
-    private ArrayList<Canteen> canteenList = new ArrayList<>();
-
-    public static CanteenManager getInstance() {
-        if (instance == null) {
-            instance = new CanteenManager();
-        }
-        return instance;
-    }
-
-    public void createCanteen(Long officeId) {
-        canteenList.add(new Canteen(officeId));
-    }
-
-    public Canteen findCanteenByOfficeId(Long officeId) {
-      return canteenList.stream().filter(c-> c.getOfficeId().equals(officeId)).findFirst().orElse(null);
-    }
-}
diff --git a/src/main/java/com/app/greenFuxes/service/canteen/CanteenService.java b/src/main/java/com/app/greenFuxes/service/canteen/CanteenService.java
deleted file mode 100644
index 7b77e8a..0000000
--- a/src/main/java/com/app/greenFuxes/service/canteen/CanteenService.java
+++ /dev/null
@@ -1,24 +0,0 @@
-package com.app.greenFuxes.service.canteen;
-
-import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
-import com.app.greenFuxes.dto.canteen.CanteenStatusDTO;
-import com.app.greenFuxes.entity.canteen.Canteen;
-import com.app.greenFuxes.entity.user.User;
-
-public interface CanteenService {
-    void addCanteen(Long officeId);
-
-    Canteen findCanteenByOfficeId(Long officeId);
-
-    String lunchUser(User user);
-
-    void finishLunch(User user);
-
-    void kickGreedy();
-
-    void restartDay(User user);
-
-    void configureCanteen(User user, CanteenSettingDTO canteenSettingDTO);
-
-    CanteenStatusDTO canteenStatus(User user);
-}
diff --git a/src/main/java/com/app/greenFuxes/service/canteen/CanteenServiceImpl.java b/src/main/java/com/app/greenFuxes/service/canteen/CanteenServiceImpl.java
deleted file mode 100644
index 342ddfa..0000000
--- a/src/main/java/com/app/greenFuxes/service/canteen/CanteenServiceImpl.java
+++ /dev/null
@@ -1,82 +0,0 @@
-package com.app.greenFuxes.service.canteen;
-
-import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
-import com.app.greenFuxes.dto.canteen.CanteenStatusDTO;
-import com.app.greenFuxes.entity.canteen.Canteen;
-import com.app.greenFuxes.entity.user.User;
-import com.app.greenFuxes.service.email.EmailSenderService;
-import org.springframework.beans.factory.annotation.Autowired;
-import org.springframework.scheduling.annotation.Scheduled;
-import org.springframework.stereotype.Service;
-
-@Service
-public class CanteenServiceImpl implements CanteenService {
-
-    private EmailSenderService emailSenderService;
-
-    @Autowired
-    public CanteenServiceImpl(EmailSenderService emailSenderService) {
-        this.emailSenderService = emailSenderService;
-    }
-
-    @Override
-    public void addCanteen(Long officeId) {
-        CanteenManager.getInstance().createCanteen(officeId);
-    }
-
-    @Override
-    public Canteen findCanteenByOfficeId(Long officeId) {
-        return CanteenManager.getInstance().findCanteenByOfficeId(officeId);
-    }
-
-    @Override
-    public String lunchUser(User user) {
-        if (findCanteenByOfficeId(extractOfficeIdFromUser(user)).lunchUser(user)) {
-            return "You can go to the canteen.";
-        } else {
-            return "You are placed into the queue. You are going to get notification as soon as you can go to the canteen.";
-        }
-
-    }
-
-    @Override
-    public void finishLunch(User user) {
-        User nextUser = findCanteenByOfficeId(extractOfficeIdFromUser(user)).finishLunch(user);
-        if (nextUser != null) {
-            emailSenderService.sendQueueNotificationEmail(nextUser);
-        }
-    }
-
-    @Override
-    @Scheduled(fixedRate = 60000)
-    public void kickGreedy() {
-        System.out.println("Egyél kutyyaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
-        CanteenManager.getInstance().getCanteenList().forEach(Canteen::kickGreedy);
-    }
-
-    @Override
-    public void restartDay(User user) {
-        findCanteenByOfficeId(extractOfficeIdFromUser(user)).restartDay();
-    }
-
-    @Override
-    public void configureCanteen(User user, CanteenSettingDTO canteenSettingDTO) {
-        findCanteenByOfficeId(extractOfficeIdFromUser(user)).setLunchtimeInMinute(canteenSettingDTO.getLunchtimeInMinute());
-        findCanteenByOfficeId(extractOfficeIdFromUser(user)).setMaxCanteenCapacity(canteenSettingDTO.getMaxCanteenCapacity());
-    }
-
-    @Override
-    public CanteenStatusDTO canteenStatus(User user) {
-        Canteen canteen = findCanteenByOfficeId(extractOfficeIdFromUser(user));
-        return new CanteenStatusDTO(canteen);
-    }
-
-    @Scheduled(cron = "0 0 0 * * ?")
-    public void setConfiguration() {
-        CanteenManager.getInstance().getCanteenList().forEach(Canteen::restartDay);
-    }
-
-    private Long extractOfficeIdFromUser(User user) {
-        return user.getReservedDate().getOffice().getId();
-    }
-}
diff --git a/src/main/java/com/app/greenFuxes/service/email/EmailSenderService.java b/src/main/java/com/app/greenFuxes/service/email/EmailSenderService.java
index 48815ae..fb00753 100644
--- a/src/main/java/com/app/greenFuxes/service/email/EmailSenderService.java
+++ b/src/main/java/com/app/greenFuxes/service/email/EmailSenderService.java
@@ -1,11 +1,11 @@
 package com.app.greenFuxes.service.email;
 
-import com.app.greenFuxes.entity.user.ConfirmationToken;
 import com.app.greenFuxes.entity.user.User;
+import com.app.greenFuxes.entity.user.ConfirmationToken;
 
-public interface EmailSenderService {
+import java.io.IOException;
 
-    void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken);
+public interface EmailSenderService {
 
-    void sendQueueNotificationEmail(User user);
+    void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken) throws IOException;
 }
diff --git a/src/main/java/com/app/greenFuxes/service/email/EmailSenderServiceImpl.java b/src/main/java/com/app/greenFuxes/service/email/EmailSenderServiceImpl.java
index 0667be4..aad90ab 100644
--- a/src/main/java/com/app/greenFuxes/service/email/EmailSenderServiceImpl.java
+++ b/src/main/java/com/app/greenFuxes/service/email/EmailSenderServiceImpl.java
@@ -10,6 +10,7 @@ import org.springframework.stereotype.Service;
 import javax.mail.*;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeMessage;
+import java.io.IOException;
 import java.util.Properties;
 
 @Service("emailSenderService")
@@ -26,21 +27,16 @@ public class EmailSenderServiceImpl implements EmailSenderService {
     }
 
     @Override
-    public void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken) {
+    public void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken) throws IOException {
         String to = user.getEmail();
         String subject = "Complete Registration!";
+
+        // Send message
         sendHTMLEmail(generateHTMLMessage(to, subject, EmailTemplateService.VERIFICATION_EMAIL(user, confirmationToken)));
         System.out.println("Sent message successfully....");
         LOGGER.info("Sent VerificationEmailHTML successfully");
     }
 
-    @Override
-    public void sendQueueNotificationEmail(User user) {
-        String to = user.getEmail();
-        String subject = "Time to lunch!";
-        sendHTMLEmail(generateHTMLMessage(to, subject, EmailTemplateService.QUEUE_NOTIFICATION_EMAIL(user)));
-    }
-
     private Message generateHTMLMessage(String to, String subject, String template) {
         // Sender's email ID needs to be mentioned
         String from = System.getenv("EMAIL_USERNAME");
diff --git a/src/main/java/com/app/greenFuxes/service/email/EmailTemplateService.java b/src/main/java/com/app/greenFuxes/service/email/EmailTemplateService.java
index 8debf45..2c6a0be 100644
--- a/src/main/java/com/app/greenFuxes/service/email/EmailTemplateService.java
+++ b/src/main/java/com/app/greenFuxes/service/email/EmailTemplateService.java
@@ -12,24 +12,14 @@ import java.io.InputStream;
 public class EmailTemplateService {
     private static String CONFIRMATION_MIDDLE_URL = "/users/confirm?token=";
 
-    public static String VERIFICATION_EMAIL(User user, ConfirmationToken confirmationToken) {
+    public static String VERIFICATION_EMAIL(User user, ConfirmationToken confirmationToken) throws IOException {
         String htmlString = readHtml("verificationEmailTemplate.html");
         String htmlReplacedData = htmlString.replace("[USERNAME]", user.getUserName()).replace("[URL_AND_TOKEN]", SecurityConstant.APP_BASE_URL + CONFIRMATION_MIDDLE_URL + confirmationToken.getConfirmationToken());
         return htmlReplacedData;
     }
 
-    public static String QUEUE_NOTIFICATION_EMAIL(User user) {
-        String htmlString = readHtml("queueNotificationTemplate.html");
-        String htmlReplacedData = htmlString.replace("[USERNAME]", user.getUserName());
-        return htmlReplacedData;
-    }
-
-    private static String readHtml(String filename) {
-        try {
-            InputStream is = new ClassPathResource(filename).getInputStream();
-            return IOUtils.toString(is);
-        } catch (IOException e) {
-            return null;
-        }
+    private static String readHtml(String filename) throws IOException {
+        InputStream is = new ClassPathResource(filename).getInputStream();
+        return IOUtils.toString(is);
     }
 }
diff --git a/src/main/java/com/app/greenFuxes/service/office/OfficeService.java b/src/main/java/com/app/greenFuxes/service/office/OfficeService.java
deleted file mode 100644
index f276b90..0000000
--- a/src/main/java/com/app/greenFuxes/service/office/OfficeService.java
+++ /dev/null
@@ -1,10 +0,0 @@
-package com.app.greenFuxes.service.office;
-
-import com.app.greenFuxes.entity.office.Office;
-
-public interface OfficeService {
-
-  Office create() throws Exception;
-
-  void setHeadCount(int headcount) throws Exception;
-}
\ No newline at end of file
diff --git a/src/main/java/com/app/greenFuxes/service/office/OfficeServiceImpl.java b/src/main/java/com/app/greenFuxes/service/office/OfficeServiceImpl.java
deleted file mode 100644
index 145625d..0000000
--- a/src/main/java/com/app/greenFuxes/service/office/OfficeServiceImpl.java
+++ /dev/null
@@ -1,45 +0,0 @@
-package com.app.greenFuxes.service.office;
-
-import com.app.greenFuxes.entity.office.Office;
-import com.app.greenFuxes.repository.officeRepository.OfficeRepository;
-
-import java.util.List;
-import java.util.Optional;
-
-import org.springframework.beans.factory.annotation.Autowired;
-import org.springframework.stereotype.Service;
-
-@Service
-public class OfficeServiceImpl implements OfficeService {
-
-    private final OfficeRepository officeRepository;
-
-    @Autowired
-    public OfficeServiceImpl(
-            OfficeRepository officeRepository) {
-        this.officeRepository = officeRepository;
-    }
-
-    @Override
-    public Office create() throws Exception {
-        List<Office> list = (List<Office>) officeRepository.findAll();
-        if (list.size() == 0) {
-            Office office = new Office();
-            return officeRepository.save(office);
-        } else {
-            return null;
-        }
-    }
-
-    @Override
-    public void setHeadCount(int headcount) throws Exception {
-        if (headcount != 0 && headcount >= 0) {
-            Optional<Office> temp = officeRepository.findById(1L);
-            if (temp.isPresent()) {
-                Office office = temp.get();
-                office.setCapacity(headcount);
-                officeRepository.save(office);
-            }
-        }
-    }
-}
diff --git a/src/main/java/com/app/greenFuxes/service/user/UserServiceImpl.java b/src/main/java/com/app/greenFuxes/service/user/UserServiceImpl.java
index 4588090..4c43d10 100644
--- a/src/main/java/com/app/greenFuxes/service/user/UserServiceImpl.java
+++ b/src/main/java/com/app/greenFuxes/service/user/UserServiceImpl.java
@@ -164,13 +164,12 @@ public class UserServiceImpl implements UserService, UserDetailsService {
 
   @EventListener(ApplicationReadyEvent.class)
   public void createFirstAdmin() {
-
-    createUser("user", encodePassword("user"), Role.ROLE_USER);
-    createUser("admin", encodePassword("admin"), Role.ROLE_SUPER_ADMIN);
+    createUser("user", "user", Role.ROLE_USER);
+    createUser("admin", "admin", Role.ROLE_SUPER_ADMIN);
   }
 
   public void createUser(String userName, String password, Role role) {
-    User user = new User(userName, password, role.name(),role.getAuthorities(), true, true);
+    User user = new User(userName, password, role.name(), true, true);
     userRepository.save(user);
   }
 
diff --git a/src/main/resources/application.properties b/src/main/resources/application.properties
index a104870..473aa7d 100644
--- a/src/main/resources/application.properties
+++ b/src/main/resources/application.properties
@@ -2,7 +2,7 @@
 spring.datasource.url=${DATASOURCE_URL}
 spring.datasource.username=${DATASOURCE_USERNAME}
 spring.datasource.password=${DATASOURCE_PASSWORD}
-spring.jpa.hibernate.ddl-auto=create-drop
+spring.jpa.hibernate.ddl-auto=update
 spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
 spring.logging.level.org.hibernate.SQL=debug
 spring.jpa.show-sql=true
\ No newline at end of file
diff --git a/src/main/resources/queueNotificationTemplate.html b/src/main/resources/queueNotificationTemplate.html
deleted file mode 100644
index fea1841..0000000
--- a/src/main/resources/queueNotificationTemplate.html
+++ /dev/null
@@ -1,10 +0,0 @@
-<!DOCTYPE html>
-<html lang="en">
-<head>
-    <meta charset="UTF-8">
-    <title>Title</title>
-</head>
-<body>
-<h1>Dear [USERNAME] go to lunch!</h1>
-</body>
-</html>
\ No newline at end of file
