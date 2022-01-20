package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

/*
 * Examples of command-line parameters:
 * 
 *  -h
 *  -i resources/examples/ex4.4body.txt -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl ftcg
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl nlug
 *
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.factories.BasicBodyBuilder;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.FallingToCenterGravityBuilder;
import simulator.factories.MassLosingBodyBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoGravityBuilder;
import simulator.model.Body;
import simulator.model.GravityLaws;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;

public class Main {
	// default dimension of vector
	public static final int DIM = 2;

	// default values for some parameters
	//
	private final static Double _dtimeDefaultValue = 2500.0;
	private static final Integer _stepsDefaultValue = 150;

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _dtime = null;
	private static Integer _steps = null;
	private static String _inFile = null;
	private static JSONObject _gravityLawsInfo = null;
	private static String _outFile;
	private static String _mode;

	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<GravityLaws> _gravityLawsFactory;

	private static void init() {
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>();
		bodyBuilders.add(new BasicBodyBuilder());
		bodyBuilders.add(new MassLosingBodyBuilder());
		_bodyFactory = new BuilderBasedFactory<>(bodyBuilders);

		ArrayList<Builder<GravityLaws>> GLBuilders = new ArrayList<>();
		GLBuilders.add(new NewtonUniversalGravitationBuilder());
		GLBuilders.add(new FallingToCenterGravityBuilder());
		GLBuilders.add(new NoGravityBuilder());
		_gravityLawsFactory = new BuilderBasedFactory<>(GLBuilders);

	}

	private static void parseArgs(String[] args) throws Exception {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			// NOTA: Añadir opcion
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseModeOption(line);
			parseHelpOption(line, cmdLineOptions);
			if (line.hasOption("i"))
				parseInFileOption(line);
			else if(_mode.equals("batch"))
				throw new ParseException("BatchMode needs an input file.");
			else _inFile = "";
			if (_mode.equals("batch")) {
				parseOutFileOption(line);
				parseStepsOption(line);
			}
			parseDeltaTimeOption(line);
			parseGravityLawsOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();
		// NOTA: Añadir opcion

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file_gravityLawsFactory
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());

		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
				.desc("Output file, where output is written. Default\n" + "value: the standard output.").build());

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());
		// steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg().desc(
				"An integer representing the number of simulation steps. Default value: " + _stepsDefaultValue + ".")
				.build());

		cmdLineOptions
				.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Choose Mode: Batch or GUI").build());

		// gravity laws -- there is a workaround to make it work even when
		// _gravityLawsFactory is null.
		//
		String gravityLawsValues = "N/A";
		String defaultGravityLawsValue = "N/A";
		if (_gravityLawsFactory != null) {
			gravityLawsValues = "";
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gravityLawsValues.length() > 0) {
					gravityLawsValues = gravityLawsValues + ", ";
				}
				gravityLawsValues = gravityLawsValues + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
			}
			defaultGravityLawsValue = _gravityLawsFactory.getInfo().get(0).getString("type");
		}
		cmdLineOptions.addOption(Option.builder("gl").longOpt("gravity-laws").hasArg()
				.desc("Gravity laws to be used in the simulator. Possible values: " + gravityLawsValues
						+ ". Default value: '" + defaultGravityLawsValue + "'.")
				.build());

		return cmdLineOptions;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
			_inFile = line.getOptionValue("i");
			if (_inFile != null) {
				if (!MyStringUtils.isValidFilename(_inFile))
					throw new ParseException("That's not a valid input filename!");
				else if (!MyStringUtils.fileExists(_inFile))
					throw new ParseException("The input file doesn't exist!");
				else if (!MyStringUtils.isReadable(_inFile))
					throw new ParseException("The intput file is not readable!");
			}
	}

	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
		if (_outFile != null && !MyStringUtils.isValidFilename(_inFile))
			throw new ParseException("That's not a valid input filename!");

	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static void parseStepsOption(CommandLine line) throws ParseException {
		String st = line.getOptionValue("s", _stepsDefaultValue.toString());
		try {
			_steps = Integer.parseInt(st);
			assert (_steps > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid steps value: " + st);
		}
	}

	private static void parseGravityLawsOption(CommandLine line) throws ParseException {

		// this line is just a work around to make it work even when _gravityLawsFactory
		// is null, you can remove it when've defined _gravityLawsFactory
		if (_gravityLawsFactory == null)
			return;

		String gl = line.getOptionValue("gl", _gravityLawsFactory.getInfo().get(0).getString("type"));
		if (gl != null) {
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gl.equals(fe.getString("type"))) {
					_gravityLawsInfo = fe;
					break;
				}
			}
			if (_gravityLawsInfo == null) {
				throw new ParseException("Invalid gravity laws: " + gl);
			}
		} else {
			_gravityLawsInfo = _gravityLawsFactory.getInfo().get(0);
		}
	}

	private static void parseModeOption(CommandLine line) throws Exception {
		_mode = line.getOptionValue("m");

		if (_mode == null)
			_mode = "batch";
		else if (!_mode.toLowerCase().equals("batch") && !_mode.toLowerCase().equals("gui"))
			throw new ParseException("That mode doesn't exist!" + System.lineSeparator());
	}

	private static void startBatchMode() throws Exception {
		// create and connect components, then start the simulator
		GravityLaws gl;
		PhysicsSimulator sim = null;
		try {
			gl = _gravityLawsFactory.createInstance(_gravityLawsInfo);
			sim = new PhysicsSimulator(gl, _dtime);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		Controller controller = new Controller(sim, _bodyFactory, _gravityLawsFactory);
		if (_inFile == null) {
			System.out.println("Invalid input file");
			System.exit(1);
		}
		InputStream is = new FileInputStream(new File(_inFile));
		try {
			controller.loadBodies(is);
		} catch (JSONException exc) {
			System.out.println(exc.getMessage());
		}
		OutputStream out = _outFile == null ? System.out : new FileOutputStream(new File(_outFile));
		controller.run(_steps, out);
	}

	private static void startGUIMode() throws Exception {
		// create and connect components, then start the simulator
		GravityLaws gl;
		PhysicsSimulator sim = null;

		try {
			gl = _gravityLawsFactory.createInstance(_gravityLawsInfo);
			sim = new PhysicsSimulator(gl, _dtime);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		Controller controller = new Controller(sim, _bodyFactory, _gravityLawsFactory);
		if (!_inFile.isEmpty()) {
			InputStream is = new FileInputStream(new File(_inFile));
			try {
				controller.loadBodies(is);
			} catch (JSONException exc) {
				System.out.println(exc.getMessage());
			}
		}
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				new MainWindow(controller);

			}
		});
	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		if (_mode.equals("batch"))
			startBatchMode();
		else startGUIMode();
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
