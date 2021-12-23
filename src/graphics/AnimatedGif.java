/**
 * 
 */
package graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;

/** This code is based on an example from a thread in Oracle's Java forums: 
 * https://forums.oracle.com/forums/thread.jspa?threadID=1262385.  I've 
 * revised and commented it.
 * @author will
 */
public final class AnimatedGif {
	// Can't instantiate.
	private AnimatedGif() {}
	
	/** Configure the metadata for one frame in the animated GIF.
	 * 
	 * @param meta The metadata to write.
	 * @param delayTime The delay time as a numeric string in hundredths of a second.
	 * @param imageIndex The index of the image in the animation.
	 */
    private static void configure(IIOMetadata meta, String delayTime, int imageIndex) {
    	// FIRST, make sure that it's the right format.
        String metaFormat = meta.getNativeMetadataFormatName();
 
        if (!"javax_imageio_gif_image_1.0".equals(metaFormat)) {
            throw new IllegalArgumentException(
                    "Unfamiliar gif metadata format: " + metaFormat);
        }
 
        Node root = meta.getAsTree(metaFormat);
 
        // NEXT, Find the GraphicControlExtension node
        Node child = root.getFirstChild();
        while (child != null) {
            if ("GraphicControlExtension".equals(child.getNodeName())) {
                break;
            }
            child = child.getNextSibling();
        }
 
        IIOMetadataNode gce = (IIOMetadataNode) child;
        gce.setAttribute("userInputFlag", "FALSE");
        gce.setAttribute("delayTime", delayTime);
 
        // NEXT, only the first node needs the ApplicationExtensions node
        if (imageIndex == 0) {
            IIOMetadataNode aes =
                    new IIOMetadataNode("ApplicationExtensions");
            IIOMetadataNode ae =
                    new IIOMetadataNode("ApplicationExtension");
            ae.setAttribute("applicationID", "NETSCAPE");
            ae.setAttribute("authenticationCode", "2.0");
            byte[] uo = new byte[]{
                //last two bytes is an unsigned short (little endian) that
                //indicates the the number of times to loop.
                //0 means loop forever.
                0x1, 0x0, 0x0
            };
            ae.setUserObject(uo);
            aes.appendChild(ae);
            root.appendChild(aes);
        }
 
        // NEXT, save the metadata.
        try {
            meta.setFromTree(metaFormat, root);
        } catch (IIOInvalidTreeException e) {
            //shouldn't happen
            throw new Error(e);
        }
    }
 
    /** Save an array of frames, all of the same size, to a file as
     * an animated GIF.  Adapted from code via GeoffTitmus on
     * http://forums.sun.com/thread.jspa?messageID=9988198.
     * 
     * @param frames A list of frames to save, in time order.
     * @param delayTime The delay between frames as a numeric string; it seems to be in
     * hundredths of a second, because "50" is supposed to be 2 frames/second.
     * @param file The file to which the GIF should be written.
     * @throws Exception on file error.
     */
    public static void saveAnimation(List<BufferedImage> frames, String delayTime, File file) throws Exception {
    	// FIRST, create an ImageWriter and an ImageOutputStream on the file,
    	// so that we can save the data.
        ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();
 
        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
        iw.setOutput(ios);
        iw.prepareWriteSequence(null);
 
        // NEXT, add the requisite metadata to each frame, and write it to
        // the file.
        int i = 0;
        for (BufferedImage src : frames) {
            ImageWriteParam iwp = iw.getDefaultWriteParam();
            IIOMetadata metadata = iw.getDefaultImageMetadata(
                    new ImageTypeSpecifier(src), iwp);
 
            configure(metadata, delayTime, i);
 
            IIOImage ii = new IIOImage(src, null, metadata);
            iw.writeToSequence(ii, (ImageWriteParam) null);
            
            i++;
        }
 
        // NEXT, complete the image and close the stream.
        iw.endWriteSequence();
        ios.close();
    }

}
