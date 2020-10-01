package net.haesleinhuepf.clijx.simpleitk;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLHostImageBuffer;
import net.haesleinhuepf.clij.coremem.ContiguousMemoryInterface;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.coremem.interop.JNAInterop;
import net.haesleinhuepf.clij.coremem.interop.NIOBuffersInterop;
import net.haesleinhuepf.clij.coremem.offheap.OffHeapMemory;
import net.haesleinhuepf.clij2.CLIJ2;
import org.itk.simple.*;

public class CLIJSimpleITKUtilities {

    // workaround classes
    static class SWIG_FLOAT extends SWIGTYPE_p_float{
        public static long getPointer(SWIGTYPE_p_float t) {
            return SWIGTYPE_p_float.getCPtr(t);
        }
    }

    static class SWIG_USHORT extends SWIGTYPE_p_unsigned_short{
        public static long getPointer(SWIGTYPE_p_unsigned_short t) {
            return SWIGTYPE_p_unsigned_short.getCPtr(t);
        }
    }

    static class SWIG_UBYTE extends SWIGTYPE_p_unsigned_char{
        public static long getPointer(SWIGTYPE_p_unsigned_char t) {
            return SWIGTYPE_p_unsigned_char.getCPtr(t);
        }
    }


    public static Image clijToITK(CLIJ2 clij2, ClearCLBuffer input) {
        long bytesPerPixel = 0;
        long length = input.getWidth() * input.getHeight() * input.getDepth();
        Image image = null;
        long pointer = 0;

        // create output image depending on type and retrieve pointer
        if (input.getNativeType() == NativeTypeEnum.Float) {
            bytesPerPixel = 4;
            if (input.getDimension() == 3) {
                image = new Image(input.getWidth(), input.getHeight(), input.getDepth(), PixelIDValueEnum.sitkFloat32);
            } else {
                image = new Image(input.getWidth(), input.getHeight(), PixelIDValueEnum.sitkFloat32);
            }

            SWIGTYPE_p_float bufferAsFloat = image.getBufferAsFloat();
            pointer = SWIG_FLOAT.getPointer(bufferAsFloat);
        } else
        if (input.getNativeType() == NativeTypeEnum.UnsignedShort) {
            bytesPerPixel = 2;
            if (input.getDimension() == 3) {
                image = new Image(input.getWidth(), input.getHeight(), input.getDepth(), PixelIDValueEnum.sitkUInt16);
            } else {
                image = new Image(input.getWidth(), input.getHeight(), PixelIDValueEnum.sitkUInt16);
            }

            SWIGTYPE_p_unsigned_short bufferAsUInt16 = image.getBufferAsUInt16();
            pointer = SWIG_USHORT.getPointer(bufferAsUInt16);
        } else if (input.getNativeType() == NativeTypeEnum.UnsignedByte) {
            bytesPerPixel = 1;
            if (input.getDimension() == 3) {
                image = new Image(input.getWidth(), input.getHeight(), input.getDepth(), PixelIDValueEnum.sitkUInt8);
            } else {
                image = new Image(input.getWidth(), input.getHeight(), PixelIDValueEnum.sitkUInt8);
            }

            SWIGTYPE_p_unsigned_char bufferAsUInt8 = image.getBufferAsUInt8();
            pointer = SWIG_UBYTE.getPointer(bufferAsUInt8);
        } else {
            System.out.println("Warning: CLIJ Type not (yet) supported. Converting to Float instead: " + input.getNativeType());
            ClearCLBuffer temp = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
            clij2.copy(input, temp);
            image = clijToITK(clij2, temp);
            temp.close();
            return image;
        }

        // copy pixels
        OffHeapMemory offHeapMemory = OffHeapMemory.wrapPointer(JNAInterop.getJNAPointer(pointer), length * bytesPerPixel);
        input.writeTo(offHeapMemory, true);
        return image;
    }

    public static ClearCLBuffer convertFloat(CLIJ2 clij2, ClearCLBuffer input) {
        ClearCLBuffer input_float = input;
        if (input.getNativeType() != clij2.Float) {
            input_float = clij2.create(input.getDimensions(), clij2.Float);
            clij2.copy(input, input_float);
        }
        return input_float;
    }


    public static ClearCLBuffer itkToCLIJ(CLIJ2 clij2, Image image) {
        long bytesPerPixel = 0;

        // determine memory size
        long length = image.getWidth() * image.getHeight();
        if (image.getDimension() == 3) {
            length = length * image.getDepth();
        }

        ClearCLBuffer buffer = null;
        long pointer = 0;

        // create output image depending on type; retrieve pointer
        if (image.getPixelID() == PixelIDValueEnum.sitkFloat32) {
            bytesPerPixel = 4;
            buffer = clij2.create(getDimensions(image), clij2.Float);
            SWIGTYPE_p_float bufferAsFloat = image.getBufferAsFloat();
            pointer = SWIG_FLOAT.getPointer(bufferAsFloat);
        } else if (image.getPixelID() == PixelIDValueEnum.sitkUInt16) {
            bytesPerPixel = 2;
            buffer = clij2.create(getDimensions(image), clij2.UnsignedShort);
            SWIGTYPE_p_unsigned_short bufferAsUInt16 = image.getBufferAsUInt16();
            pointer = SWIG_USHORT.getPointer(bufferAsUInt16);
        } else if (image.getPixelID() == PixelIDValueEnum.sitkUInt8) {
            bytesPerPixel = 1;
            buffer = clij2.create(getDimensions(image), clij2.UnsignedByte);
            SWIGTYPE_p_unsigned_char bufferAsUInt8 = image.getBufferAsUInt8();
            pointer = SWIG_UBYTE.getPointer(bufferAsUInt8);
        } else {
            System.out.println("Warning: ITK Type not (yet) supported. Converting to Float instead: " + image.getPixelID());
            Image temp = SimpleITK.cast(image, PixelIDValueEnum.sitkFloat32);
            return itkToCLIJ(clij2, temp);
        }

        // copy pixels
        OffHeapMemory offHeapMemory = OffHeapMemory.wrapPointer(JNAInterop.getJNAPointer(pointer), length * bytesPerPixel);
        buffer.readFrom(offHeapMemory, true);
        return buffer;
    }

    private static long[] getDimensions(Image image) {
        if (image.getDimension() == 2) {
            return new long[]{image.getWidth(), image.getHeight()};
        } else {
            return new long[]{image.getWidth(), image.getHeight(), image.getDepth()};
        }
    }

    public static VectorUInt32 packRadii(Integer radius_x, Integer radius_y, Integer radius_z, int dimension) {
        long [] radii = new long[dimension];
        radii[0] = radius_x;
        radii[1] = radius_y;
        if (radii.length > 2) {
            radii[2] = radius_z;
        }
        return new VectorUInt32(radii);
    }

    public static VectorDouble packRadii(Double radius_x, Double radius_y, Double radius_z, int dimension) {
        double [] radii = new double[dimension];
        radii[0] = radius_x;
        radii[1] = radius_y;
        if (radii.length > 2) {
            radii[2] = radius_z;
        }
        return new VectorDouble(radii);
    }

    public static VectorDouble packRadii(Float radius_x, Float radius_y, Float radius_z, int dimension) {
        double [] radii = new double[dimension];
        radii[0] = radius_x;
        radii[1] = radius_y;
        if (radii.length > 2) {
            radii[2] = radius_z;
        }
        return new VectorDouble(radii);
    }

    public static synchronized boolean runAndCatch(Runnable r) {
        try {
            r.run();
        } catch (Exception e ){
            System.err.println(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
