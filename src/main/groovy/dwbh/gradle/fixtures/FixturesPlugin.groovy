package dwbh.gradle.fixtures

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin exposing two tasks to load and truncate the database
 *
 * <ul>
 *     <li>fixtures-load</li>
 *     <li>fixtures-clean</li>
 * </ul>
 *
 * @since 0.1.0
 */
class FixturesPlugin implements Plugin<Project> {

	static final String TASK_NAME_CLEAN = 'fixtures-clean'

	static final String TASK_NAME_LOAD = 'fixtures-load'

	/**
	 * Default directory where to find the fixtures to load the database
	 *
	 * @since 0.1.0
	 */
	static final String DEFAULT_CLEAN_DIR = new File('fixtures/clean')

	/**
	 * Default directory where to find the fixtures to clean the database
	 *
	 * @since 0.1.0
	 */
	static final String DEFAULT_LOAD_DIR = new File('etc/fixtures/load')

	/**
	 * Default file to read database configuration from
	 *
	 * @since 0.1.0
	 */
	static final File DEFAULT_CONFIG_FILE = new File('etc/fixtures/fixtures.yaml')

	@Override
	void apply(Project project) {
		project.extensions.create('fixtures', FixturesExtension)

		project.tasks.create(TASK_NAME_LOAD, FixturesTask) { it.description = 'loads all fixtures' }
		project.tasks.create(TASK_NAME_CLEAN, FixturesTask) { it.description = 'wipes out all fixtures' }

		project.afterEvaluate {
			FixturesExtension extension = project
					.extensions
					.getByType(FixturesExtension)

			File configFile = Optional
					.ofNullable(extension.configFile)
					.map { project.file(it) }
					.orElse(DEFAULT_CONFIG_FILE)

			File loadDir = Optional
					.ofNullable(extension.loadDir)
					.map { project.file(it) }
					.orElse(DEFAULT_LOAD_DIR)

			File cleanDir = Optional.ofNullable(extension.cleanDir)
					.map { project.file(it) }
					.orElse(DEFAULT_CLEAN_DIR)

			project.tasks.getByName(TASK_NAME_LOAD).configure { FixturesTask t ->
				t.inputDir = loadDir
				t.configFile = configFile
			}

			project.tasks.getByName(TASK_NAME_CLEAN).configure { FixturesTask t ->
				t.inputDir = cleanDir
				t.configFile = configFile
			}
		}
	}
}