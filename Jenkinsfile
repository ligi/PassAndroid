stage 'assemble'
node {
 checkout scm
 sh "./gradlew clean assembleWithMapsWithAnalyticsforPlay"
}

stage 'lint'
node {
 checkout scm
 sh "./gradlew lintWithMapsWithAnalyticsforPlayRelease"
 publishHTML(target:[allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'android/build/outputs/', reportFiles: 'lint-results-withMapsWithAnalyticsForPlayRelease.html', reportName: 'Lint reports'])
}

stage 'UITest'
node {
 sh "./gradlew spoonWithMapsWithAnalyticsforPlay"
}