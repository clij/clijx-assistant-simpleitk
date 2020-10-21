package net.haesleinhuepf.clijx.simpleitk;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import org.itk.simple.*;

import java.nio.Buffer;

public class CLIJSimpleITKUtilities {
    public static Image clijToITK(CLIJ2 clij2, ClearCLBuffer cl_buffer) {
        Image itk_image = null;

        Buffer float_buffer = null;

        // create output image depending on type and retrieve pointer
        if (cl_buffer.getNativeType() == NativeTypeEnum.Float) {
            if (cl_buffer.getDimension() == 3) {
                itk_image = new Image(cl_buffer.getWidth(), cl_buffer.getHeight(), cl_buffer.getDepth(), PixelIDValueEnum.sitkFloat32);
            } else {
                itk_image = new Image(cl_buffer.getWidth(), cl_buffer.getHeight(), PixelIDValueEnum.sitkFloat32);
            }

            float_buffer = itk_image.getBufferAsByteBuffer().asFloatBuffer();
        } else if (cl_buffer.getNativeType() == NativeTypeEnum.UnsignedShort) {
            if (cl_buffer.getDimension() == 3) {
                itk_image = new Image(cl_buffer.getWidth(), cl_buffer.getHeight(), cl_buffer.getDepth(), PixelIDValueEnum.sitkUInt16);
            } else {
                itk_image = new Image(cl_buffer.getWidth(), cl_buffer.getHeight(), PixelIDValueEnum.sitkUInt16);
            }

            float_buffer = itk_image.getBufferAsByteBuffer().asCharBuffer();
        } else if (cl_buffer.getNativeType() == NativeTypeEnum.UnsignedByte) {
            if (cl_buffer.getDimension() == 3) {
                itk_image = new Image(cl_buffer.getWidth(), cl_buffer.getHeight(), cl_buffer.getDepth(), PixelIDValueEnum.sitkUInt8);
            } else {
                itk_image = new Image(cl_buffer.getWidth(), cl_buffer.getHeight(), PixelIDValueEnum.sitkUInt8);
            }

            float_buffer = itk_image.getBufferAsByteBuffer();
        } else {
            System.out.println("Warning: CLIJ Type not (yet) supported. Converting to Float instead: " + cl_buffer.getNativeType());
            ClearCLBuffer temp = clij2.create(cl_buffer.getDimensions(), NativeTypeEnum.Float);
            clij2.copy(cl_buffer, temp);
            itk_image = clijToITK(clij2, temp);
            temp.close();
            return itk_image;
        }

        // copy pixels
        cl_buffer.writeTo(float_buffer, true);
        return itk_image;
    }

    public static ClearCLBuffer convertFloat(CLIJ2 clij2, ClearCLBuffer input) {
        ClearCLBuffer input_float = input;
        if (input.getNativeType() != clij2.Float) {
            input_float = clij2.create(input.getDimensions(), clij2.Float);
            clij2.copy(input, input_float);
        }
        return input_float;
    }


    public static ClearCLBuffer itkToCLIJ(CLIJ2 clij2, Image itk_image) {
        // determine memory size
        ClearCLBuffer cl_buffer = null;
        Buffer float_buffer = null;

        // create output image depending on type; retrieve pointer
        if (itk_image.getPixelID() == PixelIDValueEnum.sitkFloat32) {
            cl_buffer = clij2.create(getDimensions(itk_image), clij2.Float);
            float_buffer = itk_image.getBufferAsByteBuffer().asFloatBuffer();
        } else if (itk_image.getPixelID() == PixelIDValueEnum.sitkUInt16) {
            cl_buffer = clij2.create(getDimensions(itk_image), clij2.UnsignedShort);
            float_buffer = itk_image.getBufferAsByteBuffer().asCharBuffer();
        } else if (itk_image.getPixelID() == PixelIDValueEnum.sitkUInt8) {
            cl_buffer = clij2.create(getDimensions(itk_image), clij2.UnsignedByte);
            float_buffer = itk_image.getBufferAsByteBuffer();
        } else {
            System.out.println("Warning: ITK Type not (yet) supported. Converting to Float instead: " + itk_image.getPixelID());
            Image temp = SimpleITK.cast(itk_image, PixelIDValueEnum.sitkFloat32);
            return itkToCLIJ(clij2, temp);
        }

        // copy pixels
        cl_buffer.readFrom(float_buffer, true);
        return cl_buffer;
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
