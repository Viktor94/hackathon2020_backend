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
    stage('Test - develop to master') {
      when {
        branch 'develop'
      }
      steps {
        sh './gradlew build --rerun-tasks'
      }
    }
    stage('Test - features to develop') {
      when {
        not {
          anyOf {
            branch 'develop';
            branch 'master'
          }
        }
      }
      steps {
        sh './gradlew build --rerun-tasks'
      }
    }
    stage('Deploy docker image') {
      when {
          branch 'master'
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
      when {
          branch 'master'
      }
      steps {
        withAWS(credentials:'benebp-aws', region: 'eu-west-3') {
          sh 'aws s3 cp ./Dockerrun.aws.json \
          s3://$S3_BUCKET/backend/$BUILD_ID/Dockerrun.aws.json'
          sh 'aws elasticbeanstalk create-application-version \
          --application-name "$APP_NAME" \
          --version-label greenfuxes-$BUILD_ID \
          --source-bundle S3Bucket="$S3_BUCKET",S3Key="backend/$BUILD_ID/Dockerrun.aws.json" \
          --auto-create-application'
          sh 'aws elasticbeanstalk update-environment \
          --application-name "$APP_NAME" \
          --environment-name $ENV_NAME \
          --version-label greenfuxes-$BUILD_ID'
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

