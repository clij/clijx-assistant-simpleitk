
open("C:/structure/data/blobs.tif");
run("CLIJ2 Macro Extensions", "cl_device=");

// median
image1 = getTitle();
Ext.CLIJ2_push(image1);

// Gaussian Blur
Ext.CLIJx_simpleITKGaussianBlur(image1, image2, 15);

// show result
Ext.CLIJ2_pull(image2);
