package org.apache.velocity.io;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
 


import java.io.IOException;

/**
 * <p>
 * The actions and template data in a JSP page is written using the
 * JspWriter object that is referenced by the implicit variable out which
 * is initialized automatically using methods in the PageContext object.
 * <p>
 * This abstract class emulates some of the functionality found in the
 * java.io.BufferedWriter and java.io.PrintWriter classes,
 * however it differs in that it throws java.io.IOException from the print
 * methods while PrintWriter does not.
 * <p><B>Buffering</B>
 * <p>
 * The initial JspWriter object is associated with the PrintWriter object
 * of the ServletResponse in a way that depends on whether the page is or
 * is not buffered. If the page is not buffered, output written to this
 * JspWriter object will be written through to the PrintWriter directly,
 * which will be created if necessary by invoking the getWriter() method
 * on the response object. But if the page is buffered, the PrintWriter
 * object will not be created until the buffer is flushed and
 * operations like setContentType() are legal. Since this flexibility
 * simplifies programming substantially, buffering is the default for JSP
 * pages.
 * <p>
 * Buffering raises the issue of what to do when the buffer is
 * exceeded. Two approaches can be taken:
 * <ul>
 * <li>
 * Exceeding the buffer is not a fatal error; when the buffer is
 * exceeded, just flush the output.
 * <li>
 * Exceeding the buffer is a fatal error; when the buffer is exceeded,
 * raise an exception.
 * </ul>
 * <p>
 * Both approaches are valid, and thus both are supported in the JSP
 * technology. The behavior of a page is controlled by the autoFlush
 * attribute, which defaults to true. In general, JSP pages that need to
 * be sure that correct and complete data has been sent to their client
 * may want to set autoFlush to false, with a typical case being that
 * where the client is an application itself. On the other hand, JSP
 * pages that send data that is meaningful even when partially
 * constructed may want to set autoFlush to true; such as when the
 * data is sent for immediate display through a browser. Each application
 * will need to consider their specific needs.
 * <p>
 * An alternative considered was to make the buffer size unbounded; but,
 * this had the disadvantage that runaway computations would consume an
 * unbounded amount of resources.
 * <p>
 * The "out" implicit variable of a JSP implementation class is of this type.
 * If the page directive selects autoflush="true" then all the I/O operations
 * on this class shall automatically flush the contents of the buffer if an
 * overflow condition would result if the current operation were performed
 * without a flush. If autoflush="false" then all the I/O operations on this
 * class shall throw an IOException if performing the current operation would
 * result in a buffer overflow condition.
 *
 * @see java.io.Writer
 * @see java.io.BufferedWriter
 * @see java.io.PrintWriter
 */

abstract public class JspWriter extends java.io.Writer {

    /**
     * constant indicating that the Writer is not buffering output
     */

    public static final int	NO_BUFFER = 0;

    /**
     * constant indicating that the Writer is buffered and is using the implementation default buffer size
     */

    public static final int	DEFAULT_BUFFER = -1;

    /**
     * constant indicating that the Writer is buffered and is unbounded; this is used in BodyContent
     */

    public static final int	UNBOUNDED_BUFFER = -2;

    /**
     * protected constructor.
     */

    protected JspWriter(int bufferSize, boolean autoFlush) {
	this.bufferSize = bufferSize;
	this.autoFlush  = autoFlush;
    }

    /**
     * Clear the contents of the buffer. If the buffer has been already
     * been flushed then the clear operation shall throw an IOException
     * to signal the fact that some data has already been irrevocably 
     * written to the client response stream.
     *
     * @throws IOException		If an I/O error occurs
     */

    abstract public void clear() throws IOException;

    /**
     * Clears the current contents of the buffer. Unlike clear(), this
     * method will not throw an IOException if the buffer has already been
     * flushed. It merely clears the current content of the buffer and
     * returns.
    *
     * @throws IOException		If an I/O error occurs
     */

    abstract public void clearBuffer() throws IOException;

    /**
     * Flush the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     * <p>
     * The method may be invoked indirectly if the buffer size is exceeded.
     * <p>
     * Once a stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown.
     *
     * @exception  IOException  If an I/O error occurs
     */

    abstract public void flush() throws IOException;

    /**
     * Close the stream, flushing it first
     * <p>
     * This method needs not be invoked explicitly for the initial JspWriter
     * as the code generated by the JSP container will automatically
     * include a call to close().
     * <p>
     * Closing a previously-closed stream, unlike flush(), has no effect.
     *
     * @exception  IOException  If an I/O error occurs
     */

    abstract public void close() throws IOException;

    /**
     * This method returns the size of the buffer used by the JspWriter.
     *
     * @return the size of the buffer in bytes, or 0 is unbuffered.
     */

    public int getBufferSize() { return bufferSize; }

    /**
     * This method returns the number of unused bytes in the buffer.
     *
     * @return the number of bytes unused in the buffer
     */

    abstract public int getRemaining();

    /**
     * This method indicates whether the JspWriter is autoFlushing.
     *
     * @return if this JspWriter is auto flushing or throwing IOExceptions on buffer overflow conditions
     */

    public boolean isAutoFlush() { return autoFlush; }

    /*
     * fields
     */

    protected int     bufferSize;
    protected boolean autoFlush;
}
