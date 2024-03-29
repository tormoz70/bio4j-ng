pipeline {

	agent any

	// using the Timestamper plugin we can add timestamps to the console log
	options {
		timestamps()
		// keep only last 10 builds
		buildDiscarder(logRotator(numToKeepStr: '10'))
        // timeout job after 60 minutes
		timeout(time: 60, unit: 'MINUTES')
	}

	tools { 
		maven "maven-3.5.4" 
		jdk "jdk8" 
	} 

	environment {
		//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
		IMAGE = readMavenPom().getArtifactId()
		VERSION = readMavenPom().getVersion()
	}

	stages {
		stage('Prepare') {
			steps {
				echo 'Some preparing ...'
				sh 'rm -f -r ${WORKSPACE}/*'
			}
		}
		
		stage('Pull') {
			steps {
				git(url: 'http://192.168.70.200/bio4j-ng/bio4j-ng-framework.git', branch: 'master-v1.5.0', credentialsId: 'jenkins')
				git(url: 'http://192.168.70.200/bio4j-ng/bio4j-ng-framework.git', branch: 'master-v2.1.0', credentialsId: 'jenkins')
				git(url: 'http://192.168.70.200/bio4j-ng/bio4j-ng-framework.git', branch: 'master-v2.5.0', credentialsId: 'jenkins')
			}
		}

		stage('Build') {
			steps {
				sh 'mvn -Dmaven.test.skip=true clean install'
			}
		}
		stage('Publish') {
			steps {
				 echo 'sh cp /target/ ...'
			}
		}
	}
	
	post {
		/*
		* These steps will run at the end of the pipeline based on the condition.
		* Post conditions run in order regardless of their place in pipeline
		* 1. always - always run
		* 2. changed - run if something changed from last run
		* 3. aborted, success, unstable or failure - depending on status
		*/
		always {
			echo "I AM ALWAYS first"
		}
		changed {
			echo "CHANGED is run second"
		}
		aborted {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED are exclusive of each other"
		}
		success {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
		}
		unstable {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
		}
		failure {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
		}
	}
}


