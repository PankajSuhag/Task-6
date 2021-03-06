job("Job1_groovy"){
        description("this job will copy the file in folder ")
        scm {
                 github('pankajsuhag/task-6' , 'master')
             }
        triggers {
                scm("* * * * *")
                
        }

	steps {
		shell(' sudo cp -vrf * /root/task6/ ')
              }
}


job("Job2_groovy"){
        description("this Job will create deployment for website and expose deployment")
        
        triggers {
	        upstream('J1_groovy', 'SUCCESS')
	  }

      steps {
	shell('''
               if sudo ls  /root/task6/  | grep php
               then
               echo " Going to Start deployment for php code "
               sudo cd /root/task6/
	       sudo ls
               sudo kubectl apply -f /root/task6/jen_kube_php.yml
               else
	       echo "There is no php file"
               fi
           
               if sudo ls /root/task6/  | grep html  
               echo " Going to Start deployment for html code "  
               sudo cd /root/task6/
               sudo kubectl apply -f /root/task6/jen_kube_html.yml
               else
	       echo "There is no html file"
               fi ''')
     }
}


  job("Job3_groovy")
	{
	  steps{
	    shell('''
	status=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.101:31000)
	if [[ $status == 200 ]]
	then
	    echo "Running HTML Website"
	    exit 0
	else
	     exit 1
	fi
         
        status=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.101:32000)
	if [[ $status == 200 ]]
	then
	    echo "Running Php Website"
	    exit 0
	else
	     exit 1
	fi
	     ''')
	  }
	  
	  triggers {
	        upstream('Job2_groovy', 'SUCCESS')
	  }
	  
	  publishers {
	        extendedEmail {
	            recipientList('pankaj.suhag.397@gmail.com')
	            defaultSubject('Job status')
	          	attachBuildLog(attachBuildLog = true)
	            defaultContent('Status Report')
	            contentType('text/html')
	            triggers {
	                always {
	                    subject('build Status')
	                    content('Body')
	                    sendTo {
	                        developers()
	                        recipientList()
	                    }
			       }
		       }
		   }
	  }
 }











