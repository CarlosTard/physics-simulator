package simulator.launcher;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

public class MyStringUtils {

	// returns true if string argument is a valid filename
	public static boolean isValidFilename(String filename) {
		try {
			Paths.get(filename);
			return true;
		} catch (InvalidPathException ipe) {
			return false;
		}
	}

	// returns true if file with that name exists (in which case, it may not be
	// accessible )
	public static boolean fileExists(String filename) {
		try {
			Path path = Paths.get(filename);
			return Files.exists(path) && Files.isRegularFile(path);
		} catch (InvalidPathException ipe) {
			return false; // filename invalid => file cannot exist
		}
	}

	// returns true if file with that name exists and is readable
	public static boolean isReadable(String filename) {
		try {
			Path path = Paths.get(filename);
			return Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path);
		} catch (InvalidPathException ipe) {
			return false; // filename invalid => file cannot exist , never mind be readable
		}
	}
}
