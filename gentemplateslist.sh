rm ./src/x/mvmn/gp2srv/web/templates/templates_list.properties
touch ./src/x/mvmn/gp2srv/web/templates/templates_list.properties
for TNAME in $(ls -1 -r ./src/x/mvmn/gp2srv/web/templates)
do
	if [ "$TNAME" != "test.vm" ] && [ "$TNAME" != "VM_global_library.vm" ] && [ "$TNAME" != "templates_list.properties" ]
	then
		echo "$TNAME=$TNAME" >> ./src/x/mvmn/gp2srv/web/templates/templates_list.properties
	fi
done
