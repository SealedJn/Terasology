/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.paths.PathManager;

import java.nio.file.Path;

/**
 * Helper class to have LWJGL loading logic in a central spot
 */
public final class LWJGLHelper {

    private static final Logger logger = LoggerFactory.getLogger(LWJGLHelper.class);

    private LWJGLHelper() {
    }

    /**
     * Used on initializing the game environment, either for playing or for running unit tests
     */
    public static void initNativeLibs() {
        initLibraryPaths();
    }

    private static void initLibraryPaths() {
        final Path path;
        switch (OS.get()) {
            case WINDOWS:
                path = PathManager.getInstance().getNativesPath().resolve("windows");
                break;
            case MACOSX:
                path = PathManager.getInstance().getNativesPath().resolve("macosx");
                break;
            case LINUX:
                path = PathManager.getInstance().getNativesPath().resolve("linux");
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operating system: " + System.getProperty("os.name"));
        }
        final String natives = path.toAbsolutePath().toString();
        System.setProperty("org.lwjgl.librarypath", natives);
        System.setProperty("net.java.games.input.librarypath", natives);  // libjinput
    }
}
