package it.redhat.demo.task;

import org.infinispan.Cache;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.model.Project;

/**
 * @author Fabio Massimo Ercoli
 */
public class IncrementProjectCodeTask extends CacheTask implements ServerTask<Project> {

	private static final Logger LOG = LoggerFactory.getLogger( IncrementProjectCodeTask.class );

	private static final String CACHE_PARAM_KEY = "name";

	private TaskContext taskContext;

	@Override
	public String getName() {
		return IncrementProjectCodeTask.class.getSimpleName();
	}

	@Override
	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	@Override
	public Project call() throws Exception {

		Cache<String, Project> cache = getCache( taskContext );
		String projectName = (String) taskContext.getParameters().get().get( CACHE_PARAM_KEY );

		LOG.info( "Executing task. Updating project: {}", projectName );

		Project project = cache.get( projectName );

		if (project == null) {
			LOG.warn( "Cache Entry Project not found!" );
			return null;
		}

		project.setCode( project.getCode() + 1 );
		project = cache.put( projectName, project );
		if (project == null) {
			LOG.info( "Force not return value is not enabled so get the current value here" );
			project = cache.get( projectName );
		}

		LOG.info( "Executed task. Project {} updated", project );

		return project;
	}

}
