
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
    publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "android/build/spoon", reportFiles: '*/debug/index.html', reportName: 'Spoon'])
    step([$class: 'JUnitResultArchiver', testResults: 'android/build/spoon/*/debug/junit-reports/*.xml'])   
   }
  }

 stage 'lint'
  try {
   sh "./gradlew lint${flavorCombination}Release"
  } catch(err) {
   currentBuild.result = FAILURE
  } finally {
   androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '', unHealthy: ''
  }

 stage 'test'
  try {
   sh "./gradlew test${flavorCombination}DebugUnitTest"
  } catch(err) {
   currentBuild.result = FAILURE
  } finally {
   step([$class: 'JUnitResultArchiver', testResults: 'android/build/test-results/*/*.xml'])
   publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/reports/tests/', reportFiles: "*/index.html", reportName: 'UnitTest'])
  }
  
 stage 'assemble'
  sh "./gradlew assemble${flavorCombination}Release"
  archive 'android/build/outputs/apk/*'
  archive 'android/build/outputs/mapping/*/release/mapping.txt'
     
}