rm -rf ./target
mvn clean package dependency:copy-dependencies
mv ./target/*.jar ./target/dependency/
mv ./target/dependency ./target/gp2srv-bin
