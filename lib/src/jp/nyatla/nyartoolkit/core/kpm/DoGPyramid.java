package jp.nyatla.nyartoolkit.core.kpm;

import jp.nyatla.nyartoolkit.core.kpm.vision.detectors.GaussianScaleSpacePyramid;

public class DoGPyramid
{
	public DoGPyramid()
	{
		this.mNumOctaves=0;
		this.mNumScalesPerOctave=0;
	}
    
    /**
     * Allocate the pyramid.
     */
    public void alloc(GaussianScaleSpacePyramid pyramid)
    {
        assert pyramid.size()>0;//ASSERT(pyramid.size() > 0, "Pyramid is not allocated");
        
//        ImageType type = pyramid->get(0, 0).type();
        int width = pyramid.get(0, 0).getWidth();
        int height = pyramid.get(0, 0).getHeight();
        
        this.mNumOctaves = pyramid.numOctaves();;
        this.mNumScalesPerOctave = pyramid.numScalesPerOctave()-1;
        
        
        // Allocate DoG images 同一サイズのDoG画像ピラミッドを作る
        mImages=new KpmImage[this.mNumOctaves*this.mNumScalesPerOctave];
        for(int i = 0; i < this.mNumOctaves; i++) {
            for(int j = 0; j < this.mNumScalesPerOctave; j++) {
//                mImages[i*mNumScalesPerOctave+j].alloc(type, width>>i, height>>i, AUTO_STEP, 1);
                mImages[i*mNumScalesPerOctave+j]=new KpmImage(width>>i, height>>i);//多分あってるんじゃないか的な
            }
        }    	
    }
    
    /**
     * Compute the Difference-of-Gaussian from a Gaussian Pyramid.
     */
    public void compute(GaussianScaleSpacePyramid pyramid)
    {
//        ASSERT(mImages.size() > 0, "Laplacian pyramid has not been allocated");
//        ASSERT(pyramid->numOctaves() > 0, "Pyramid does not contain any levels");
//        ASSERT(dynamic_cast<const BinomialPyramid32f*>(pyramid), "Only binomial pyramid is supported");
        
        for(int i = 0; i < this.mNumOctaves; i++) {
            for(int j = 0; j < this.mNumScalesPerOctave; j++) {
                difference_image_binomial(this.get(i, j),
				  pyramid.get(i, j),
				  pyramid.get(i, j+1));
            }
        }    	
    }
    
    /**
     * Get a Laplacian image at a level in the pyramid.
     */
    public KpmImage get(int octave, int scale) {
    	return mImages[octave*mNumScalesPerOctave+scale];
    }
    
    /**
     * Get vector of images.
     */
    public KpmImage[] images(){
    	return this.mImages;
    }
    
    /**
     * Get a Laplacian image at an index.
     */
    public KpmImage get(int index) {
    	return mImages[index];
    }
    
    /**
     * Get the number of octaves and scales.
     */
    public int numOctaves() {
    	return this.mNumOctaves;
    }
    public int numScalePerOctave() {
    	return this.mNumScalesPerOctave;
    }
    public int size() {
    	return this.mImages.length;
    }
    
    /**
     * Get the octave from the Laplacian image index.
     */
    public int octaveFromIndex(int index)
    {
//        ASSERT(index < mImages.size(), "Index is out of range");
        return (int)KpmMath.round(KpmMath.log2((mImages[0].getWidth()/this.mImages[index].getWidth())));
    }
    
    /**
     * Get the scale from the Laplacian image index.
     */
    public int scaleFromIndex(int index){
    	return index%this.mNumScalesPerOctave;
    }
    

    
    // DoG images
    private KpmImage[] mImages;
    
    // Number of octaves and scales
    private int mNumOctaves;
    private int mNumScalesPerOctave;
    
    /**
     * Compute the difference image.
     *
     * d = im1 - im2
     */
    private void difference_image_binomial(KpmImage d,KpmImage im1,KpmImage im2)
    {
//        ASSERT(d.type() == IMAGE_F32, "Only F32 images supported");
//        ASSERT(im1.type() == IMAGE_F32, "Only F32 images supported");
//        ASSERT(im2.type() == IMAGE_F32, "Only F32 images supported");
//        ASSERT(d.channels() == 1, "Only single channel images supported");
//        ASSERT(im1.channels() == 1, "Only single channel images supported");
//        ASSERT(im2.channels() == 1, "Only single channel images supported");
//        ASSERT(d.width() == im2.width(), "Images must have the same width");
//        ASSERT(d.height() == im2.height(), "Images must have the same height");
//        ASSERT(im1.width() == im2.width(), "Images must have the same width");
//        ASSERT(im1.height() == im2.height(), "Images must have the same height");
        
        // Compute diff
    	float[] p0=(float[])d.getBuffer();
    	float[] p1=(float[])im1.getBuffer();
    	float[] p2=(float[])im2.getBuffer();
        for(int i = 0; i < im1.getHeight(); i++) {
//            float* p0 = d.get<float>(i);
//            const float* p1 = im1.get<float>(i);
//            const float* p2 = im2.get<float>(i);
            int p0_ptr=d.get(i);
            int p1_ptr=im1.get(i);
            int p2_ptr= im2.get(i);
            for(int j = 0; j < im1.getWidth(); j++) {
                p0[j+p0_ptr] = p1[j+p1_ptr]-p2[j+p2_ptr];
            }
        }
    }
}
