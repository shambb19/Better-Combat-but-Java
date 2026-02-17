This program requires input .txt files to run. 

**Part 1: Writing PC's**
PC's are more complicated in that they require stats, weapons, and spells. Below is an example of a correctly formatted PC:

<img width="487" height="211" alt="image" src="https://github.com/user-attachments/assets/22322a08-fb1f-49bf-8f5b-99be7119f729" />

This is relatively self-explanatory, with the exception of the stat block. Values for each stat are put in parentheses. Plus values represent proficiency in that particular stat.
Note that these values can be in any order, but proficiency bonus MUST ALWAYS come before the stat block. I tried and failed to code around this issue, so you'll have to be aware of it.

Note that for readability, weapons and spells may be split into multiple lines and will be read correctly. For example:

<img width="156" height="52" alt="image" src="https://github.com/user-attachments/assets/6b3c303e-ecd1-4e6a-ad3e-507795b547ad" />

is also valid.

Spell names tend to be multiple words, and these should be separated with underscores:

spells=chill_touch/poison_spray/shocking_grasp/ray_of_sickness

**Part 2: Writing NPC's**
This is far simpler than the above. Here is a correct example:

<img width="144" height="130" alt="image" src="https://github.com/user-attachments/assets/61dad888-82fb-4352-8762-d1ee317286c4" />

**Part 3: Putting it All Together**

Chained combinations of NPCs and PCs make up a battle scenario. Any PCs must be uploaded in a separate .txt file, which will look something like the following:

<img width="482" height="482" alt="image" src="https://github.com/user-attachments/assets/e0267721-9bdc-4cdb-9cd1-3dbc219ceedb" />

This will be entered in the "Party" prompt in the upload screen. Note that no headers are required, though the brackets absolutely are. 
The rest of the information will be uploaded in the other prompt.

<img width="181" height="424" alt="image" src="https://github.com/user-attachments/assets/148b4e1e-6981-47a4-af27-10fcec3c2cff" />

The <Allies> and <Enemies> tags are required at the beginning and end of their respective team lists.
Party members are written as listed above. The only allowed party members are those present in your uploaded Party .txt document

Let me know if you have any questions, comments, or concerns!
