import jwt from "jsonwebtoken";

export const generateToken = (userID) => {
    const payload = { id: userID };
    const token = jwt.sign(payload, process.env.TOKEN_SECRET, { expiresIn: '365d' });
    return token;
}