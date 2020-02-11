

public class Worker extends Thread {
    int id;
    int numThreads;
    int kernelSize;
    float[] kernel;
    PpmImage image;
    PpmImage output;

    Worker(int id, int numThreads, PpmImage input, PpmImage output, int kernelSize, float[] kernel) {
        this.id = id;
        this.numThreads = numThreads;
        this.kernelSize = kernelSize;
        this.kernel = kernel;
        this.image = input;
        this.output = output;
    }

    public void run() {

        int ik, jk, kernelIndex, dataIndex;
        float sum = 0, currentPixel;
        // widthOffset to handle the right slice per thread
        int widthOffset = (this.image.width / numThreads);
        // sliceWidth to handle the width slice
        int sliceWidth = (this.id == numThreads - 1) ? (this.image.width / numThreads) + (this.image.width % numThreads)
                : (this.image.width / numThreads);

        for (int i = 0; i < this.image.height; i++) {
            for (int j = 0; j < sliceWidth; j++) {
                for (int c = 0; c < this.image.channels; c++) {
                    for (int ii = 0; ii < this.kernelSize; ii++) {
                        ik = ((i - this.kernelSize / 2 + ii) < 0) ? 0 : Math.min((i - this.kernelSize / 2 + ii), this.image.height - 1);
                        for (int jj = 0; jj < this.kernelSize; jj++) {
                            jk = ((j - this.kernelSize / 2 + jj + widthOffset * this.id) < 0) ? 0 :
                                    ((j - this.kernelSize / 2 + jj + widthOffset * this.id) > this.image.width - 1) ?
                                            this.image.width - 1 - widthOffset * this.id : j - this.kernelSize / 2 + jj;
                            dataIndex = (ik * this.image.width + jk + widthOffset * this.id) * this.image.channels + c;
                            currentPixel = this.image.data[dataIndex];
                            kernelIndex = (this.kernelSize - 1 - ii) * this.kernelSize + (this.kernelSize - 1 - jj);
                            sum += (currentPixel * this.kernel[kernelIndex]);
                        }
                    }
                    output.data[(i * this.image.width + j + widthOffset * this.id) * this.image.channels + c] = sum;
                    sum = 0;
                }
            }
        }
    }
}
