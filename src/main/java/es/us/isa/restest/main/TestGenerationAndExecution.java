package es.us.isa.restest.main;

import es.us.isa.restest.configuration.pojos.TestConfigurationObject;
import es.us.isa.restest.coverage.CoverageGatherer;
import es.us.isa.restest.coverage.CoverageMeter;
import es.us.isa.restest.generators.ARTestCaseGenerator;
import es.us.isa.restest.generators.AbstractTestCaseGenerator;
import es.us.isa.restest.generators.RandomTestCaseGenerator;
import es.us.isa.restest.reporting.AllureReportManager;
import es.us.isa.restest.reporting.StatsReportManager;
import es.us.isa.restest.runners.RESTestRunner;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.testcases.writers.RESTAssuredWriter;
import es.us.isa.restest.util.*;
import org.codehaus.groovy.transform.SourceURIASTTransformation;

import java.util.List;

import static es.us.isa.restest.configuration.TestConfigurationIO.loadConfiguration;
import static es.us.isa.restest.inputs.semantic.ARTEInputGenerator.szEndpoint;
import static es.us.isa.restest.util.FileManager.createDir;
import static es.us.isa.restest.util.FileManager.deleteDir;


/*
 * This class show the basic workflow of test case generation -> test case execution -> test reporting
 */
public class TestGenerationAndExecution {

	// Properties file with configuration settings
	private static String propertiesFilePath = "src/test/resources/Restcountries/restcountries.properties";

	private static Integer numTestCases; 								// Number of test cases per operation
	private static String OAISpecPath; 									// Path to OAS specification file
	private static OpenAPISpecification spec; 							// OAS specification
	private static String confPath; 									// Path to test configuration file
	private static String targetDirJava; 								// Directory where tests will be generated.
	private static String packageName; 									// Package name.
	private static String experimentName; 								// Used as identifier for folders, etc.
	private static String testClassName; 								// Name prefix of the class to be generated
	private static Boolean enableInputCoverage; 						// Set to 'true' if you want the input coverage report.
	private static Boolean enableOutputCoverage; 						// Set to 'true' if you want the input coverage report.
	private static Boolean enableCSVStats; 								// Set to 'true' if you want statistics in a CSV file.
	private static Boolean deletePreviousResults; 						// Set to 'true' if you want previous CSVs and Allure reports.
	private static Float faultyRatio; 									// Percentage of faulty test cases to generate. Defaults to 0.1
	private static Integer totalNumTestCases; 							// Total number of test cases to be generated (-1 for infinite loop)

	private static String generator; 									// Generator (RT: Random testing, CBT:Constraint-based testing)
	private static Boolean logToFile;									// If 'true', log messages will be printed to external files
	private static boolean executeTestCases;							// If 'false', test cases will be generated but not executed
	private static boolean allureReports;								// If 'true', Allure reports will be generated
	private static boolean checkTestCases;								// If 'true', test cases will be checked with OASValidator before executing them
	private static String proxy;										// Proxy to use for all requests in format host:port

	// For Constraint-based testing and AR Testing:
	private static Float faultyDependencyRatio; 						// Percentage of faulty test cases due to dependencies to generate.
	private static Integer reloadInputDataEvery; 						// Number of requests using the same randomly generated input data
	private static Integer inputDataMaxValues; 							// Number of values used for each parameter when reloading input data

	// For AR Testing only:
	private static String similarityMetric;								// The algorithm to measure the similarity between test cases
	private static Integer numberCandidates;							// Number of candidate test cases per AR iteration
	// ARTE
	private static Boolean learnRegex;									// Set to 'true' if you want RESTest to automatically generate Regular expressions that filter the semantically generated input data
	private static boolean secondPredicateSearch;
	private static int maxNumberOfPredicates;                			// MaxNumberOfPredicates = AdditionalPredicates + 1
	private static int minimumValidAndInvalidValues;
	private static String metricToUse;
	private static Double minimumValueOfMetric;
	private static int maxNumberOfTriesToGenerateRegularExpression;


	public static void main(String[] args) throws RESTestException {
		// ONLY FOR LOCAL COPY OF DBPEDIA
		if (szEndpoint.contains("localhost") || szEndpoint.contains("127.0.0.1"))
			System.setProperty("http.maxConnections", "10000");
		readParameterValues();
		createDir(targetDirJava);
		AbstractTestCaseGenerator generator = createGenerator(); // Test case generator
		IWriter writer = createWriter(); // Test case writer
		StatsReportManager statsReportManager = createStatsReportManager(); // Stats reporter
		AllureReportManager reportManager = createAllureReportManager(); // Allure test case reporter

		RESTestRunner runner = new RESTestRunner(testClassName, targetDirJava, packageName, learnRegex,
				secondPredicateSearch, spec, confPath, generator, writer,
				reportManager, statsReportManager);
		runner.setExecuteTestCases(executeTestCases);
		runner.setAllureReport(allureReports);


		while (totalNumTestCases == -1 || runner.getNumTestCases() < totalNumTestCases) {
			// Generate unique test class name to avoid the same class being loaded everytime
			String id = IDGenerator.generateTimeId();
			String className = testClassName + "_" + id;
			((RESTAssuredWriter) writer).setClassName(className);
			((RESTAssuredWriter) writer).setTestId(id);
			runner.setTestClassName(className);
			runner.setTestId(id);
			// Test case generation + execution + test report generation
			runner.run();
		}

	}

	// Create a test case generator
	private static AbstractTestCaseGenerator createGenerator(){
		// Load specification
		spec = new OpenAPISpecification(OAISpecPath);
		// Load configuration
		TestConfigurationObject conf;

		conf = loadConfiguration(confPath, spec);
		ARTestCaseGenerator gen = new ARTestCaseGenerator(spec, conf, numTestCases);
		gen.setFaultyDependencyRatio(faultyDependencyRatio);
		gen.setInputDataMaxValues(inputDataMaxValues);
		gen.setReloadInputDataEvery(reloadInputDataEvery);
		gen.setDiversity(similarityMetric);
		gen.setNumberOfCandidates(numberCandidates);
		gen.setFaultyRatio(faultyRatio);
		gen.setCheckTestCases(checkTestCases);
//		ARTestCaseGenerator gen = null;
//		AbstractTestCaseGenerator gen = null;
//		gen = new RandomTestCaseGenerator(spec, conf, numTestCases);
//		((RandomTestCaseGenerator) gen).setFaultyRatio(faultyRatio);
		return gen;
	}

	// Create a writer for RESTAssured
	private static IWriter createWriter() {
		String basePath = spec.getSpecification().getServers().get(0).getUrl();
		RESTAssuredWriter writer = new RESTAssuredWriter(OAISpecPath, confPath, targetDirJava, testClassName, packageName,
				basePath, logToFile);
		writer.setLogging(true);
		writer.setAllureReport(true);
		writer.setEnableStats(enableCSVStats);
		writer.setEnableOutputCoverage(enableOutputCoverage);
		writer.setAPIName(experimentName);
		writer.setProxy(proxy);
		return writer;
	}

	// Create an Allure report manager
	private static AllureReportManager createAllureReportManager() {
		AllureReportManager arm = null;
		if(executeTestCases) {
			String allureResultsDir = readParameterValue("allure.results.dir") + "/" + experimentName;
			String allureReportDir = readParameterValue("allure.report.dir") + "/" + experimentName;

			// Delete previous results (if any)
			if (deletePreviousResults) {
				deleteDir(allureResultsDir);
				deleteDir(allureReportDir);
			}

			//Find auth property names (if any)
			List<String> authProperties = AllureAuthManager.findAuthProperties(spec, confPath);
			arm = new AllureReportManager(allureResultsDir, allureReportDir, authProperties);
			arm.setEnvironmentProperties(propertiesFilePath);
			arm.setHistoryTrend(true);
		}
		return arm;
	}

	// Create an statistics report manager
	private static StatsReportManager createStatsReportManager() {
		String testDataDir = readParameterValue("data.tests.dir") + "/" + experimentName;
		String coverageDataDir = readParameterValue("data.coverage.dir") + "/" + experimentName;

		// Delete previous results (if any)
		if (deletePreviousResults) {
			deleteDir(testDataDir);
			deleteDir(coverageDataDir);

			// Recreate directories
			createDir(testDataDir);
			createDir(coverageDataDir);
		}

		CoverageMeter coverageMeter = enableInputCoverage || enableOutputCoverage ? new CoverageMeter(new CoverageGatherer(spec)) : null;

		return new StatsReportManager(testDataDir, coverageDataDir, enableCSVStats, enableInputCoverage,
					enableOutputCoverage, coverageMeter, secondPredicateSearch, maxNumberOfPredicates,
					minimumValidAndInvalidValues, metricToUse, minimumValueOfMetric,
					maxNumberOfTriesToGenerateRegularExpression);
	}


	// Read the parameter values from the .properties file. If the value is not found, the system looks for it in the global .properties file (config.properties)
	private static void readParameterValues() {
		logToFile = Boolean.parseBoolean(readParameterValue("logToFile"));
		if(logToFile) {
			setUpLogger();
		}
		generator = readParameterValue("generator");
		OAISpecPath = readParameterValue("oas.path");
		confPath = readParameterValue("conf.path");
		targetDirJava = readParameterValue("test.target.dir");
		experimentName = readParameterValue("experiment.name");
		packageName = experimentName;
		if (readParameterValue("experiment.execute") != null) {
			executeTestCases = Boolean.parseBoolean(readParameterValue("experiment.execute"));
		}
		if (readParameterValue("allure.report") != null) {
			allureReports = Boolean.parseBoolean(readParameterValue("allure.report"));
		}
		if (readParameterValue("proxy") != null) {
			proxy = readParameterValue("proxy");
			if ("null".equals(proxy) || proxy.split(":").length != 2)
				proxy = null;
		}
		if (readParameterValue("testcases.check") != null)
			checkTestCases = Boolean.parseBoolean(readParameterValue("testcases.check"));
		testClassName = readParameterValue("testclass.name");
		if (readParameterValue("testsperoperation") != null)
			numTestCases = Integer.parseInt(readParameterValue("testsperoperation"));
		if (readParameterValue("numtotaltestcases") != null)
			totalNumTestCases = Integer.parseInt(readParameterValue("numtotaltestcases"));
		if (readParameterValue("reloadinputdataevery") != null)
			reloadInputDataEvery = Integer.parseInt(readParameterValue("reloadinputdataevery"));
		if (readParameterValue("inputdatamaxvalues") != null)
			inputDataMaxValues = Integer.parseInt(readParameterValue("inputdatamaxvalues"));
		if (readParameterValue("coverage.input") != null)
			enableInputCoverage = Boolean.parseBoolean(readParameterValue("coverage.input"));
		if (readParameterValue("coverage.output") != null)
			enableOutputCoverage = Boolean.parseBoolean(readParameterValue("coverage.output"));
		if (readParameterValue("stats.csv") != null)
			enableCSVStats = Boolean.parseBoolean(readParameterValue("stats.csv"));
		if (readParameterValue("deletepreviousresults") != null)
			deletePreviousResults = Boolean.parseBoolean(readParameterValue("deletepreviousresults"));
		if (readParameterValue("similarity.metric") != null)
			similarityMetric = readParameterValue("similarity.metric");
		if (readParameterValue("art.number.candidates") != null)
			numberCandidates = Integer.parseInt(readParameterValue("art.number.candidates"));
		if (readParameterValue("faulty.ratio") != null)
			faultyRatio = Float.parseFloat(readParameterValue("faulty.ratio"));
		if (readParameterValue("faulty.dependency.ratio") != null)
			faultyDependencyRatio = Float.parseFloat(readParameterValue("faulty.dependency.ratio"));
		// ARTE
		if (readParameterValue("learnRegex") != null)
			learnRegex = Boolean.parseBoolean(readParameterValue("learnRegex"));
		if (readParameterValue("secondPredicateSearch") != null)
			secondPredicateSearch = Boolean.parseBoolean(readParameterValue("secondPredicateSearch"));
		if (readParameterValue("maxNumberOfPredicates") != null)
			maxNumberOfPredicates = Integer.parseInt(readParameterValue("maxNumberOfPredicates"));
		if (readParameterValue("minimumValidAndInvalidValues") != null)
			minimumValidAndInvalidValues = Integer.parseInt(readParameterValue("minimumValidAndInvalidValues"));
		if (readParameterValue("metricToUse") != null)
			metricToUse = readParameterValue("metricToUse");
		if (readParameterValue("minimumValueOfMetric") != null)
			minimumValueOfMetric = Double.parseDouble(readParameterValue("minimumValueOfMetric"));
		if (readParameterValue("maxNumberOfTriesToGenerateRegularExpression") != null)
			maxNumberOfTriesToGenerateRegularExpression = Integer.parseInt(readParameterValue("maxNumberOfTriesToGenerateRegularExpression"));
	}

	// Read the parameter value from: 1) CLI; 2) the local .properties file; 3) the global .properties file (config.properties)
	private static String readParameterValue(String propertyName) {
		String value = null;
		if (PropertyManager.readProperty(propertiesFilePath, propertyName) != null) {
			System.out.println("333333333333333333333333333333333333333333333333333333333333");
			System.out.println(PropertyManager.readProperty(propertiesFilePath, propertyName));
			System.out.println("333333333333333333333333333333333333333333333333333333333333");// Read value from local .properties file
			value = PropertyManager.readProperty(propertiesFilePath, propertyName);
		}
		else if (PropertyManager.readProperty(propertyName) != null) {
			System.out.println("44444444444444444444444444444444444444444444444444444444444444444");
			System.out.println(PropertyManager.readProperty(propertyName));
			System.out.println("44444444444444444444444444444444444444444444444444444444444444444");// Read value from global .properties file
			value = PropertyManager.readProperty(propertyName);
		}
		return value;
	}

	public static TestConfigurationObject getTestConfigurationObject(){
		return loadConfiguration(confPath, spec);
	}

	public static String getExperimentName(){ return experimentName; }

	private static void setUpLogger() {
		if (Boolean.parseBoolean(readParameterValue("deletepreviousresults"))) {
			String logDataDir = readParameterValue("data.log.dir") + "/" + readParameterValue("experiment.name");
			deleteDir(logDataDir);
			createDir(logDataDir);
		}
		// Configure regular logger
		String logPath = readParameterValue("data.log.dir") + "/" + readParameterValue("experiment.name") + "/" + readParameterValue("data.log.file");
		System.setProperty("logFilename", logPath);
	}
}
