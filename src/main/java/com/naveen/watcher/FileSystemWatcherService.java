package com.naveen.watcher;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naveen.storage.StorageService;

@Service
public class FileSystemWatcherService {
	String dirName = "/Users/naveen/Documents/temp/";
	private static Logger log = LoggerFactory.getLogger(FileSystemWatcherService.class);
	private final StorageService storageService;

	@Autowired
	public FileSystemWatcherService(StorageService storageService) {
		this.storageService = storageService;
	}

	public void startFileWatcher() {

		try {
			DirectoryWatcher watchService = new DirectoryWatcherImpl(); // May
																		// throw
			watchService.register( // May throw
					new DirectoryWatcher.OnFileChangeListener() {
						@Override
						public void onFileCreate(String filePath) {
							// File created
							log.info("File created :" + filePath);						}

						@Override
						public void onFileModify(String filePath) {
							// File modified
							log.info("File modified :" + filePath);
						}

						@Override
						public void onFileDelete(String filePath) {
							// File deleted
							log.info("File deleted :" + filePath);
						}
					}, dirName

			);

			watchService.start();
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					watchService.stop();
					log.error("Main thread interrupted.");
					break;
				}
			}

		} catch (IOException e) {
			log.error("Unable to register file change listener for " + dirName);
		}

	}

}
