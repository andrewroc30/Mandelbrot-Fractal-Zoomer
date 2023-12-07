package View;

public interface CreationWindow {

    void addNewProgressBar(String filename);

    void updateProgress(String filename, int completionPercentage);

    void removeProgressBar(String filename);

}
