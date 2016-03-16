def flavorCombination='WithMapsWithAnalyticsForPlay'

stage 'assemble'
node {
 checkout scm
 sh "./gradlew clean assemble${flavorCombination}"
}

stage 'lint'
node {
 checkout scm
 sh "./gradlew lint${flavorCombination}Release"
 //publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/outputs/', reportFiles: 'lint-results-${flavorCombination}Release.html', reportName: 'Lint reports'])
}

stage 'UITest'
node {
 sh "./gradlew spoon${flavorCombination}"
  publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/spoon-output/${flavorCombination}DebugAndroidTest/', reportFiles: 'index.html', reportName: 'Spoon reports'])
}