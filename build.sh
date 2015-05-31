rm -rf ./target
mvn clean package dependency:copy-dependencies
mkdir -p ./target/gp2srv-bin/lib/
mv ./target/dependency/*.jar ./target/gp2srv-bin/lib/
mv ./target/*.jar ./target/gp2srv-bin/
