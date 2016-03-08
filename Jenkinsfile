stage 'Build'
node {
 checkout scm
 sh "./gradlew clean :android:build"
}

stage 'UITest'
node {
 sh "./gradlew spoonWithMapsWithAnalyticsforFDroid"
}