#!/bin/bash
set -e

echo "Publishing FCMS Client to Maven Central..."

./gradlew clean publishToMavenCentral \
  -x detekt \
  -x test \
  -PmavenCentralUsername=5YLmpf \
  -PmavenCentralPassword=EIEK3FZQ6zyf3aQHU6c75lotKYNWh0r00

echo ""
echo "✅ Published successfully!"
echo ""
echo "⏳ Maven Central indexing takes 15-30 minutes."
echo "   Check: https://repo1.maven.org/maven2/io/github/tellesy/fcms-client/"
echo "   Search: https://search.maven.org/artifact/io.github.tellesy/fcms-client"
