package com.naveen.watcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;

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
	private final Path dockLocation;

	@Autowired
	public FileSystemWatcherService(StorageService storageService, StorageProperties properties) {
		this.storageService = storageService;
		this.dockLocation = Paths.get(properties.getDockLocation());
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
							readFileProperty(dockLocation.resolve(filePath));
							copyFile(filePath);
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
					}, dockLocation.toString()

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
			log.error("Unable to register file change listener for " + dockLocation.toString() + "/nError:"
					+ e);
		}

	}

	private void copyFile(Path filePath) {
		try (InputStream targetStream = new FileInputStream(new File(dockLocation.resolve(filePath).toString()))) {

			storageService.store(targetStream, filePath.toString());
			// Delete file on successful file transfer
			storageService.deletefile(dockLocation.resolve(filePath));
		} catch (IOException e) {

			log.error("Unable to read file [name] " + dockLocation.resolve(filePath) + "/nError :" + e);
		}

	}

	private void readFileProperty(Path filePath) {
		try {
			PosixFileAttributes attrs = Files.readAttributes(filePath, PosixFileAttributes.class);

			log.info("directory: " + attrs.isDirectory());
			log.info("is other : " + attrs.isOther());
			log.info("regular  : " + attrs.isRegularFile());
			log.info("symlink  : " + attrs.isSymbolicLink());
			log.info("size     : " + attrs.size());
			log.info("unique id: " + attrs.fileKey());
			log.info("access time  : " + attrs.lastAccessTime());
			log.info("creation time: " + attrs.creationTime());
			log.info("modified time: " + attrs.lastModifiedTime());
			log.info("owner: " + attrs.owner());
			log.info("group: " + attrs.group());
		} catch (IOException e) {
			log.error("Unable to read file [properties] " + dockLocation.resolve(filePath)+"/nError:"+e);
		}
	}
}
