import os

# Function to replace the xmlns URL in the file content
def replace_xmlns_in_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()

    # Unfuck the version
    content = content.replace('xmlns="http://javafx.com/javafx/23.0.1"', 'xmlns="http://javafx.com/javafx/23"')

    # Write the updated content back to the file
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(content)

print("FXML Version Unfucker 0.1 (Potvrdjujem ocenu iz pythona!)")

# Iterate over all files in the current directory
for filename in os.listdir():
    if filename.endswith('.fxml'):
        file_path = os.path.join(os.getcwd(), filename)
        replace_xmlns_in_file(file_path)
        print(f"Unfucked fxml version in {filename}")
