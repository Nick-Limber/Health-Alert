import { config } from "dotenv";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));


const result = config({ path: path.resolve(__dirname, ".env") });

if (result.error) {
    console.error(" Failed to load .env file:", result.error);
} else {
    console.log(" Environment variables injected successfully.");
}

export default {};