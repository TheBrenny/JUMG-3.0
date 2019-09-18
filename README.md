# JUMG ('Jum-Gee') 3.0!
### Java Utilities for Making Games _the third_
<hr>

### Contents
 1. What is JUMG?
 2. Demo Game
 3. How to use JUMG
 4. Extra Notes
<hr>

### What is JUMG?
JUMG (pronounced 'Jum-Gee', acronymed 'Java Utilities for Making Games') is a library that contains multiple utilities to make Java Games the most efficient way. JUMG utilises the concepts of separating certain portions of code from the rest, to allow one main class to bring them altogether.

As JUMG is in development, it is necessary for a checklist to be made of what needs to be added, and what is added. This is here for simplicity's sake:

#### JUMG Development Checklist:
 1. [x] [Binary File IO (using custom protocols - so text can still be saved)](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/util/FileIO.java)
 2. [ ] String Compression
 3. [x] [Frame and Screen implementation](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/Display.java)
 4. [x] [Graphical User Interface utilities *(will be continued as I go on...)*](https://github.com/TheBrenny/JUMG-3.0/tree/master/src/com/thebrenny/jumg/gui)
 5. [ ] [Heads Up Display simplicity](https://github.com/TheBrenny/JUMG-3.0/tree/master/src/com/thebrenny/jumg/hud)
 6. [x] [Game Engine](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/engine/Engine.java)
 7. [ ] Sprite Manager
 8. [x] [Level Manager](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/level/Level.java)
 9. [x] [AI (pathfinding and states)](https://github.com/TheBrenny/JUMG-3.0/tree/master/src/com/thebrenny/jumg/entities/ai)
 10. [ ] Sound Manager
 11. [x] [Key Bindings](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/input/KeyBindings.java)
 12. [x] [Entities](https://github.com/TheBrenny/JUMG-3.0/tree/master/src/com/thebrenny/jumg/entities)
 13. [ ] [Inventory System](https://github.com/TheBrenny/JUMG-3.0/tree/master/src/com/thebrenny/jumg/items)
 13. [ ] Save Game System
 14. [ ] Questing System
 15. [x] [Messaging System (not exclusive to entities!)](https://github.com/TheBrenny/JUMG-3.0/tree/master/src/com/thebrenny/jumg/entities/messaging)
 16. [x] [Collision detection](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/level/Level.java#L229)
 17. [ ] Networking
 18. [ ] Version control and updating
 19. [x] [Startup Arguments](https://github.com/TheBrenny/JUMG-3.0/blob/master/src/com/thebrenny/jumg/util/ArgumentOrganizer.java)
 20. [ ] Bulk Debugging Classes
 21. [ ] Full JavaDocs
 22. [ ] RELEASE!

> Currently I'm working on my project for Uni while I continue to work on JUMG. This is because my project depends on JUMG. As I work through, changes will occur and enhancements will be made, thus making the changes on this list seem eratic. Just bear with me. :ok_hand:

### Demo Game
Check out the demo game which is a project for my Computer Games course at Uni, [BattleCell](https://github.com/TheBrenny/BattleCell)!

### How to use JUMG

#### ~If you only want some packages:~
 1. ~Copy the `com.brennytizer.jumg.utils` package to your workspace.~
 2. ~Copy your wanted packages across.~
 3. ~The packages should be available for use!~

_This isn't actually done. Dependencies are everywhere throughout the project._

#### If you want the full library:
 1. Eclipse
    1. Make sure the JUMG Library is a project in your workspace.
    2. Right Click your project > "Build Path" > "Configure Build Path..."
    3. "Projects" tab > "Add..."
    4. Tick the JUMG project you have made > OK > OK
    5. The packages should be available for use!
 2. VSCode
    1. Create a Workspace in VSCode and make sure the JUMG Library is a project in it.
    2. In your project, open the `.classpath` file and add the following line: `<classpathentry combineaccessrules="false" kind="src" path="/JUMG 3.0"/>`
    3. You should now be able to use and modify JUMG!

Note that these methods allow you to alter the packages, for your own personalization benefits, or to improve on JUMG. If you do make an improvement or addition that is not personalized, **submit a PR!**

### Extra Notes
JUMG is an open source tool that is freely available for modifications. Take it and throw it into eclipse, but remember that it would be nice that if you muddle around with it, that if you could throw a push request to the current JUMG build, here on GitHub.

JUMG uses the [Gnu GPL](http://www.gnu.org/licenses/gpl-3.0.txt) license to support modification of the works presented in this repository - however one should always remember to not redistribute the works under their name. I mean, come on man, that's not cool. Changes and additions to the works will be credited with a name, the work committed, and a slogan (provided that I approve of the slogan).

On another note about the license, if you need to hit the road but have already downloaded this library, and want to read the license, you can check the LICENSE.md file in the root of the library folder.

#### Disclamer
JUMG and BrennyPress are free softwares: you can
redistribute them and/or modify them under the terms of the
GNU General Public License as published by the Free Software
Foundation, either version 3 of the License any later
version.

JUMG and BrennyPress are distributed in the hope that they
will be useful, but WITHOUT ANY WARRANTY; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for
more details.

You should have received a copy of the GNU General Public
License along with this software collection. If not, see
<http://www.gnu.org/licenses/>.
