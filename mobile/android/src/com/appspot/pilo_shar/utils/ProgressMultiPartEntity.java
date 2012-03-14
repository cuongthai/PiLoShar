package com.appspot.pilo_shar.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class ProgressMultiPartEntity extends MultipartEntity {
	private final ProgressListener listener;
	
	public static interface ProgressListener
	{
		void transferred(long num);
	}
	
	public ProgressMultiPartEntity(final ProgressListener listener)
	{
		super();
		this.listener = listener;
	}
	public ProgressMultiPartEntity(final HttpMultipartMode mode, final ProgressListener listener)
	{
		super(mode);
		this.listener = listener;
	} 
	public ProgressMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener)
	{
		super(mode, boundary, charset);
		this.listener = listener;
	}
	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream,listener));
	}
	public static class CountingOutputStream extends FilterOutputStream{
		private final ProgressListener listener;
		private long transferred;
		
		public CountingOutputStream(final OutputStream out, final ProgressListener listener)
		{
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}
		
		/*
		 * is called when a segment of data sent out(non-Javadoc)
		 * @see java.io.FilterOutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte[] buffer, int offset, int length)
				throws IOException {
			int BUFFER_SIZE = 10000;
			int chunkSize;
			int currentOffset = 0;

			while (length>currentOffset) {
			chunkSize = length - currentOffset;
			if (chunkSize > BUFFER_SIZE) {
			chunkSize = BUFFER_SIZE;
			}
			out.write(buffer, currentOffset, chunkSize);
			currentOffset += chunkSize;
			this.transferred += chunkSize;
			this.listener.transferred(this.transferred);
			}
		}
		/*
		 * Sends one byte(non-Javadoc)
		 * @see java.io.FilterOutputStream#write(int)
		 */
		@Override
		public void write(int oneByte) throws IOException {
			out.write(oneByte);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
		@Override
		public void write(byte[] buffer) throws IOException {
			super.write(buffer);
			
		}
	}
	
}
