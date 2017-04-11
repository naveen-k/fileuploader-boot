package com.naveen.watcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naveen.storage.StorageProperties;
import com.naveen.storage.StorageService;

@Service
public class FileSystemWatcherService {

	private static Logger log = LoggerFactory.getLogger(FileSystemWatcherService.class);
	private final StorageService storageService;
	private final Path rootLocation;

	@Autowired
	public FileSystemWatcherService(StorageService storageService, StorageProperties properties) {
		this.storageService = storageService;
		this.rootLocation = Paths.get(properties.getlocationDockLocation());
	}

	public void startFileWatcher() {

		try {
			DirectoryWatcher watchService = new DirectoryWatcherImpl(); // May
																		// throw
			watchService.register( // May throw
					new DirectoryWatcher.OnFileChangeListener() {
						@Override
						public void onFileCreate(Path filePath) {
							// File created
							log.info("File created :" + filePath.toString());

							InputStream targetStream = null;
							try {
								targetStream = new FileInputStream(new File(rootLocation.resolve(filePath).toString()));
								storageService.store(targetStream, filePath.toString());
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								log.error("Unable to read file [name] " + rootLocation.resolve(filePath));
							}

						}

						@Override
						public void onFileModify(Path filePath) {
							// File modified
							log.info("File modified :" + filePath.toFile().getPath());
						}

						@Override
						public void onFileDelete(Path filePath) {
							// File deleted
							log.info("File deleted :" + filePath.toFile().getPath());
						}
					}, rootLocation.toString()

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
			log.error("Unable to register file change listener for " + rootLocation.toString());
		}

	}

}
