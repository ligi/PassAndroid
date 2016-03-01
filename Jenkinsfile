node {
 def flavorCombination='WithMapsWithAnalyticsForPlay'

 stage 'checkout'
  checkout scm

 stage 'UITest'
  lock('adb') {
   try {
    sh "./gradlew clean spoon${flavorCombination}"
   } catch(err) {
    currentBuild.result = FAILURE
   } finally {
     publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "android/build/spoon-output/${flavorCombination}DebugAndroidTest", reportFiles: 'index.html', reportName: 'Spoon'])
   }
  }

 stage 'assemble'
  sh "./gradlew assemble${flavorCombination}Release"
  archive 'android/build/outputs/apk/*'

 stage 'lint'
  try {
   sh "./gradlew lint${flavorCombination}Release"
  } catch(err) {
   currentBuild.result = FAILURE
  } finally {
   publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/outputs/', reportFiles: "lint-results-*Release.html", reportName: 'Lint'])
  }
 
 stage 'test'
  sh "./gradlew test${flavorCombination}DebugUnitTest"
  publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/reports/tests/', reportFiles: "*/index.html", reportName: 'UnitTest'])
}