package com.yoson.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;


public class ZipUtils {

	private ZipUtils() {
		
	}
	
	public static void decompress(String srcPath, String dest, boolean deepUnzip) throws Exception {
		File file = new File(srcPath);
		if (!file.exists()) {
			throw new RuntimeException(srcPath + "File not exists");
		}

		ZipFile zf = null;
		try {
			zf = new ZipFile(file);			
		} catch (Exception e) {
			throw new Exception(file.getAbsolutePath() + " is not a valid zip file");
		}
		Enumeration entries = zf.entries();
		ZipEntry entry = null;
		while (entries.hasMoreElements()) {
			entry = (ZipEntry) entries.nextElement();
			if (entry.isDirectory()) {
				File dir = new File(FilenameUtils.concat(dest, entry.getName()));
				dir.mkdirs();
			} else {
				File f = new File(FilenameUtils.concat(dest, entry.getName()));
				if (!f.exists()) {
					File parentDir = new File(FilenameUtils.getFullPath(f.getAbsolutePath()));
					parentDir.mkdirs();
				}
				f.createNewFile();
				InputStream is = zf.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(f);
				int count;
				byte[] buf = new byte[8192];
				while ((count = is.read(buf)) != -1) {
					fos.write(buf, 0, count);
				}
				is.close();
				fos.close();
			}
		}
		zf.close();
		if(deepUnzip) {
			Collection<File> moreZips = FileUtils.listFiles(new File(dest), new SuffixFileFilter(Arrays.asList(".zip"), IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);
			for(File zip : moreZips) {
				decompress(zip.getAbsolutePath(), FilenameUtils.concat(FilenameUtils.getFullPath(zip.getAbsolutePath()), FilenameUtils.getBaseName(zip.getAbsolutePath())), deepUnzip);
			}
		}
		
	}

	public static void doCompress(String srcFile, String zipFile) throws IOException {
		doCompress(new File(srcFile), new File(zipFile));
	}

	/**
	 * 文件压缩
	 * 
	 * @param srcFile
	 *            目录或者单个文件
	 * @param zipFile
	 *            压缩后的ZIP文件
	 */
	public static void doCompress(File srcFile, File zipFile) throws IOException {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			doCompress(srcFile, out);
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();// 记得关闭资源
		}
	}

	public static void doCompress(String filelName, ZipOutputStream out) throws IOException {
		doCompress(new File(filelName), out);
	}

	public static void doCompress(File file, ZipOutputStream out) throws IOException {
		doCompress(file, out, "");
	}

	public static void doCompress(File inFile, ZipOutputStream out, String dir) throws IOException {
		if (inFile.isDirectory()) {
			File[] files = inFile.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					String name = inFile.getName();
					if (!"".equals(dir)) {
						name = dir + "/" + name;
					}
					ZipUtils.doCompress(file, out, name);
				}
			}
		} else {
			ZipUtils.doZip(inFile, out, dir);
		}
	}

	public static void doZip(File inFile, ZipOutputStream out, String dir) throws IOException {
		String entryName = null;
		if (!"".equals(dir)) {
			entryName = dir + "/" + inFile.getName();
		} else {
			entryName = inFile.getName();
		}
		ZipEntry entry = new ZipEntry(entryName);
		out.putNextEntry(entry);

		int len = 0;
		byte[] buffer = new byte[1024];
		FileInputStream fis = new FileInputStream(inFile);
		while ((len = fis.read(buffer)) > 0) {
			out.write(buffer, 0, len);
			out.flush();
		}
		out.closeEntry();
		fis.close();
	}

}