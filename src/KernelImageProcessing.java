public class KernelImageProcessing {

    public static void main(String[] args) throws InterruptedException {
        PpmImage input = new PpmImage(), output;
        input.ppmImport(args[0]);
        String outputPath = args[1];
        int kernelSize = Integer.parseInt(args[2]);
        int numThreads = Integer.parseInt(args[3]);
        output = new PpmImage(input.width, input.height, input.channels, input.depth);
        float[] kernel = (kernelSize == 5) ? Globals.kernel5 : (kernelSize == 7) ? Globals.kernel7 : Globals.kernel3;

        long start = System.currentTimeMillis();

        Worker[] workers = new Worker[numThreads];
        for (int i = 0; i < numThreads; i++)
            workers[i] = new Worker(i, numThreads, input, output, kernelSize, kernel);
        for (int i = 0; i < numThreads; i++)
            workers[i].start();
        for (Worker worker : workers) worker.join();

        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        System.out.print((double) elapsed / 1000);

        output.ppmExport(outputPath + "output_java" + numThreads + ".ppm");
    }

}
