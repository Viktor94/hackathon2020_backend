// small instance with linux20.04lts = ami-078db6d55a16afc82

pipeline {
  agent any
  environment {
    ENV_NAME = "hackathon2020-09-greenfuxes-backend-env"
    S3_BUCKET = "hackathon2020-09-greenfuxes"
    APP_NAME = 'hackathon2020-09-greenfuxes-backend'
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
    stage('Deploy to AWS') {
      when{
          branch 'develop'
      }
      steps {
        withAWS(credentials:'benebp-aws', region: 'eu-west-3') {
          sh 'aws s3 cp ./Dockerrun.aws.json \
          s3://$S3_BUCKET/backend/$BUILD_ID/Dockerrun.aws.json'
          sh 'aws elasticbeanstalk create-application-version \
          --application-name "$APP_NAME" \
          --version-label $APP_NAME-$BUILD_ID \
          --source-bundle S3Bucket="$S3_BUCKET",S3Key="$BUILD_ID/Dockerrun.aws.json" \
          --auto-create-application'
          sh 'aws elasticbeanstalk update-environment \
          --application-name "$APP_NAME" \
          --environment-name $ENV_NAME \
          --version-label $APP_NAME-$BUILD_ID'
        }
      }
    }
  }
  // post{
  //   always{
  //     junit 'build/test-results/**/*.xml'
  //     step([$class: 'JacocoPublisher', execPattern: 'target/*.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', exclusionPattern: 'src/test*'])
  //   }
  // }
}

