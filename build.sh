rm -rf ./target
mvn clean package
GP2VERSION=$(cat pom.xml| grep version | head -n 1 | cut -d ">" -f 2 | cut -d "<" -f 1)
GP2FOLDER=gp2srv-$GP2VERSION

mkdir -p ./target/$GP2FOLDER/lib/
mv ./target/lib/*.jar ./target/$GP2FOLDER/lib/
mv ./target/*.jar ./target/$GP2FOLDER/
mv ./target/run.bat ./target/$GP2FOLDER/
chmod +x ./target/run.sh
mv ./target/run.sh ./target/$GP2FOLDER/

cd target
zip -r ./$GP2FOLDER.zip ./$GP2FOLDER
cd ..
