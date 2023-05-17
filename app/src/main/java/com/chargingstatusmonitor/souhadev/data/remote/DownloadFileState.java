package com.chargingstatusmonitor.souhadev.data.remote;

import java.io.File;

public abstract class DownloadFileState {
    public static final class Progress extends DownloadFileState {
        private final int percent;

        public Progress(int percent) {
            this.percent = percent;
        }

        public int getPercent() {
            return percent;
        }
    }

    public static final class Finished extends DownloadFileState {
        private final File file;

        public Finished(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
}
