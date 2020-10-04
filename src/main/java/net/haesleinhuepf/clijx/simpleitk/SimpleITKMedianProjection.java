package net.haesleinhuepf.clijx.simpleitk;


import ij.IJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.imglib2.img.io.IJLoader;
import org.itk.simple.Image;
import org.itk.simple.SimpleITK;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKMedianProjection")
public class SimpleITKMedianProjection extends AbstractSimpleITKCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKMedianProjection(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1])));
        return result;
    }

    public static synchronized boolean simpleITKMedianProjection(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output) {

        ClearCLBuffer transposed = clij2.create(new long[]{input.getDepth(), input.getHeight(), input.getWidth()}, input.getNativeType());
        clij2.transposeXZ(input, transposed);

        // convert to ITK
        Image itk_input = clijToITK(clij2, transposed);
        transposed.close();

        // apply SimpleITK Median Projection
        Image itk_output = SimpleITK.medianProjection(itk_input);

        // push result back
        ClearCLBuffer result = itkToCLIJ(clij2, itk_output);

        // save it in the right place
        clij2.transposeXZ(result, output);

        // clean up
        result.close();

        return true;
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(new long[]{input.getWidth(), input.getHeight()}, input.getNativeType());
    }

    @Override
    public String getDescription() {
        return "Apply SimpleITKs Median projection to an image.\n\n" +
                "See also: https://simpleitk.org/doxygen/latest/html/classitk_1_1simple_1_1MedianProjectionImageFilter.html";
    }

    @Override
    public String getAvailableForDimensions() {
        return "3D -> 2D";
    }

    @Override
    public String getCategories() {
        return "Projection";
    }
}
