// small instance with linux20.04lts = ami-078db6d55a16afc82

pipeline {
  agent any
  environment {
    registry = "hackathon2020-09-greenfuxes"
    // registryCredential = 'docker-technical-foxyfox'
    // dockerImage = ''
    // ENV_NAME = "Devma-dev"
    // S3_BUCKET = "elasticbeanstalk-eu-west-2-124429370407"
    // APP_NAME = 'Devma'
  }
  stages {
    // stage('Gradle Build') {
    //   steps {
    //     sh './gradlew build --rerun-tasks'
    //   }
    // }
    stage('Deploy docker image') {
      when{
          branch 'develop'
      }
      steps {
        script {
          image = docker.build("benebp/hackathon2020-09-greenfuxes:latest")
        }
        script{
          docker.withRegistry('', "benebp-dockerhub"){
            image.push()
          }
        }
      }
    }
    // stage('Deploy to AWS') {
    //   when{
    //       branch 'develop'
    //   }
    //   steps {
    //     withAWS(credentials:'devma-staging ', region: 'eu-west-2') {
    //       sh 'aws s3 cp ./DevDockerrun.aws.json \
    //       s3://$S3_BUCKET/$BUILD_ID/Dockerrun.aws.json'
    //       sh 'aws elasticbeanstalk create-application-version \
    //       --application-name "$APP_NAME" \
    //       --version-label devma-Dev-$BUILD_ID \
    //       --source-bundle S3Bucket="$S3_BUCKET",S3Key="$BUILD_ID/Dockerrun.aws.json" \
    //       --auto-create-application'
    //       sh 'aws elasticbeanstalk update-environment \
    //       --application-name "$APP_NAME" \
    //       --environment-name $ENV_NAME \
    //       --version-label devma-Dev-$BUILD_ID'
    //     }
    //   }
    // }
  }
  // post{
  //   always{
  //     junit 'build/test-results/**/*.xml'
  //     step([$class: 'JacocoPublisher', execPattern: 'target/*.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', exclusionPattern: 'src/test*'])
  //   }
  // }
}

