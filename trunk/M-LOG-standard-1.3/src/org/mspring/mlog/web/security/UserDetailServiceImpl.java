/**
 * 
 */
package org.mspring.mlog.web.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mspring.mlog.entity.security.Resource;
import org.mspring.mlog.entity.security.Role;
import org.mspring.mlog.entity.security.TreeItem;
import org.mspring.mlog.entity.security.User;
import org.mspring.mlog.service.security.ResourceService;
import org.mspring.mlog.service.security.RoleService;
import org.mspring.mlog.service.security.TreeItemService;
import org.mspring.mlog.service.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Gao Youbo
 * @since 2013-1-11
 * @Description
 * @TODO
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private TreeItemService treeItemService;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        Collection<GrantedAuthority> grantedAuths = obtionGrantedAuthorities(user);

        boolean enables = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new UserDetailsImpl(user.getName(), user.getPassword(), enables, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuths, user);
    }

    /**
     * 取得用户的权限
     * @param user
     * @return
     */
    private Set<GrantedAuthority> obtionGrantedAuthorities(User user) {
        Set<GrantedAuthority> authSet = new HashSet<GrantedAuthority>();
        List<Role> roles = roleService.getRolesByUser(user.getId());

        for (Role role : roles) {
            List<Resource> resources = resourceService.findResourceByRole(role.getId());
            for (Resource res : resources) {
                String name = "RES_" + res.getId() + "_" + res.getName();
                authSet.add(new GrantedAuthorityImpl(name));
            }
            
//            List<TreeItem> items = treeItemService.findTreeItemByRole(role.getId());
//            for (TreeItem item : items) {
//                String name = "ITEM_" + item.getId() + "_" + item.getName();
//                authSet.add(new GrantedAuthorityImpl(name));
//            }
        }
        return authSet;
    }

}
