import { db_pool } from "../config/db";

app.delete('/api/delete-account', async (req, res) => {
    const { email, password } = req.body;
    console.log(`Recieved delete request for ${email}`);

    //test
    if (email === "test@test.com" && password === "123")
    {
        console.log("Match found (Mocked)");
        return res.status(200).json({ message: "Mock delete successful " });
    }
    else
    {
        return res.status(401).json({ error: "Invalid creds"});
    }

    try {
        const [userRows] = await db_pool.query(
            "SELECT id FROM `users` WHERE email = ? AND password = ?", 
            [email, password]
        );

        if (userRows.length === 0) { 
            return res.status(401).json({ error: "Invalid credentials" });
        }

        const masterId = userRows[0].id;

        const connection = await db_pool.getConnection();
        await connection.beginTransaction();

        try {
            await connection.query("DELETE FROM personal_information WHERE profile_id = ?", [masterId]);
            await connection.query("DELETE FROM diets WHERE profile_id = ?", [masterId]);
            await connection.query("DELETE FROM exercise WHERE profile_id = ?", [masterId]);
        
            await connection.query("DELETE FROM `users` WHERE id = ?", [masterId]);
        
            await connection.commit();
            res.status(200).json({ message: "Account and data has been deleted permanently."});
        } catch (err) {
        await connection.rollback();
        throw err;
    } finally {
        connection.release();
    }
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error."});
    }
})