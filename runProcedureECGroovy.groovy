/*
CloudBees CD DSL: Run procedure from ec-groovy
This example illustrates
- Running a procedure from ec-groovy
- Passing arguments
- Creating a link to the spawned job
- Wait for the spawned job to complete
- Retrieve a property from the job
*/




project "DSL-Samples",{
	procedure "Procedure to run",{
		formalParameter "SleepTime", required: true, description: "Sleep time in seconds"
		step "Sleep", shell: "ec-groovy", command: '''\
			sleep $[SleepTime] * 1000
		'''.stripIndent()
	}
	procedure "Call procedure",{
		step "Call procedure", shell: "ec-groovy", command: '''\
			import com.electriccloud.client.groovy.ElectricFlow
			import com.electriccloud.client.groovy.models.ActualParameter
			ElectricFlow ef = new ElectricFlow()
	
			def params = [
				new ActualParameter('SleepTime', '10')
			]
			def JobId = ef.runProcedure(procedureName: "Procedure to run", projectName: "$[/myProject]", actualParameters: params).jobId
			// Create job link to spawned job
			ef.setProperty propertyName: "/myJob/report-urls/Called procedure job", value: "link/jobDetails/jobs/${JobId}"
			// Wait for job
			def JobStatus
			while ((JobStatus = (String) ef.getJobStatus(jobId: JobId).status) != "completed") {
				println "Job status: " + JobStatus
				sleep 2000
			}
			println ef.getProperty(propertyName: "/myJob/launchedByUser", jobId: JobId).property.value
			
		'''.stripIndent()
	}
}

