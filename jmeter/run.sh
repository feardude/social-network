#!/bin/sh

export HEAP="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m"

jmx_template_file=template.social-network.search.jmx
 threads=(1 10 100 1000)
for t in ${threads[@]}; do
	jmx_file=social-network.search.$t.jmx
	result_file=result-index-$t.csv
	
	echo "Threads:" $t
	echo "JMX file:" $jmx_file
	echo "Result file:" $result_file

	cp $jmx_template_file $jmx_file
	sed -i -e "s/{THREADS}/$t/g" $jmx_file
	sed '13!d' $jmx_file

	jmeter -n -t $jmx_file -l $result_file -e

	rm $jmx_file
	rm "${jmx_file}-e"

	echo
done
